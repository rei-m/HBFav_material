package me.rei_m.hbfavmaterial.domain.model

import android.content.SharedPreferences
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.domain.entity.UserEntity
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.infra.network.HatenaApiService
import retrofit2.HttpException
import java.net.HttpURLConnection

class UserModel(private val preferences: SharedPreferences,
                private val hatenaApiService: HatenaApiService) {

    companion object {
        private const val KEY_PREF_USER = "KEY_PREF_USER"
    }

    private val userSubject: PublishSubject<UserEntity> = PublishSubject.create()

    val user: Observable<UserEntity> = userSubject

    private val confirmCompleteRegistrationEventSubject = PublishSubject.create<Boolean>()

    val confirmCompleteRegistrationEvent: Observable<Boolean> = confirmCompleteRegistrationEventSubject

    private val completeUpdateUserEventSubject = PublishSubject.create<UserEntity>()

    val completeUpdateUserEvent: Observable<UserEntity> = completeUpdateUserEventSubject

    private val unauthorisedEventSubject = PublishSubject.create<Unit>()

    val unauthorisedEvent: Observable<Unit> = unauthorisedEventSubject

    private val errorSubject = PublishSubject.create<Unit>()

    val error: Observable<Unit> = errorSubject

    fun getUser() {
        userSubject.onNext(getUserFromPreferences())
    }

    fun confirmCompleteRegistration() {
        confirmCompleteRegistrationEventSubject.onNext(getUserFromPreferences().isCompleteSetting)
    }

    fun setUpUserId(userId: String) {

        val currentUser = getUserFromPreferences()
        if (currentUser.id == userId) {
            completeUpdateUserEventSubject.onNext(currentUser)
            return
        }

        hatenaApiService.userCheck(userId).map {
            // 原因はわからないがカンマ等の記号が入っている場合にTopページを取得しているケースがある
            // 基本的には入力時に弾く予定だが、Modelの仕様としては考慮してトップページが返ってきたら
            // 存在しないユーザー = 404として扱う
            return@map !it.contains("<title>はてなブックマーク</title>")
        }.onErrorResumeNext {
            return@onErrorResumeNext if (it is HttpException) {
                when (it.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        Single.just(false)
                    }
                    else -> {
                        Single.error(it)
                    }
                }
            } else {
                Single.error(it)
            }
        }.subscribeAsync({ isValidId ->
            if (isValidId) {
                val user = UserEntity(userId)
                preferences.edit()
                        .putString(KEY_PREF_USER, Gson().toJson(user))
                        .apply()
                completeUpdateUserEventSubject.onNext(user)
            } else {
                unauthorisedEventSubject.onNext(Unit)
            }
        }, {
            errorSubject.onNext(Unit)
        })
    }

    fun updateCheckedPostStatus(isCheckedPostBookmarkOpen: Boolean,
                                isCheckedPostBookmarkReadAfter: Boolean) {
        val user = getUserFromPreferences()
        user.isCheckedPostBookmarkOpen = isCheckedPostBookmarkOpen
        user.isCheckedPostBookmarkReadAfter = isCheckedPostBookmarkReadAfter
        preferences.edit()
                .putString(KEY_PREF_USER, Gson().toJson(user))
                .apply()
        completeUpdateUserEventSubject.onNext(user)
    }

    private fun getUserFromPreferences(): UserEntity {
        val userJsonString = preferences.getString(KEY_PREF_USER, null)
        return if (userJsonString != null) {
            Gson().fromJson(userJsonString, UserEntity::class.java)
        } else {
            UserEntity(id = "")
        }
    }
}
