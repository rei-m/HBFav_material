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

    var user: UserEntity = getUserFromPreferences()
        private set(value) {
            field = value
            storeUserToPreferences(value)
            userUpdatedEventSubject.onNext(value)
        }

    private val userUpdatedEventSubject = PublishSubject.create<UserEntity>()
    private val unauthorisedEventSubject = PublishSubject.create<Unit>()
    private val errorSubject = PublishSubject.create<Unit>()

    val userUpdatedEvent: Observable<UserEntity> = userUpdatedEventSubject
    val unauthorisedEvent: Observable<Unit> = unauthorisedEventSubject
    val error: Observable<Unit> = errorSubject

    fun setUpUserId(userId: String) {

        hatenaApiService.userCheck(userId).map {
            // 原因はわからないがカンマ等の記号が入っている場合にTopページを取得しているケースがある
            // Modelの仕様としては考慮してトップページが返ってきたら 存在しないユーザー = 404として扱う
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
                user = UserEntity(userId)
            } else {
                unauthorisedEventSubject.onNext(Unit)
            }
        }, {
            errorSubject.onNext(Unit)
        })
    }

    private fun getUserFromPreferences(): UserEntity {
        val userJsonString = preferences.getString(KEY_PREF_USER, null)
        return if (userJsonString != null) {
            Gson().fromJson(userJsonString, UserEntity::class.java)
        } else {
            UserEntity(id = "")
        }
    }

    private fun storeUserToPreferences(user: UserEntity) {
        preferences.edit()
                .putString(KEY_PREF_USER, Gson().toJson(user))
                .apply()
    }
}
