package me.rei_m.hbfavmaterial.model

import android.content.SharedPreferences
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.infra.network.HatenaBookmarkService
import me.rei_m.hbfavmaterial.model.entity.UserEntity
import retrofit2.HttpException
import java.net.HttpURLConnection

class UserModel(private val preferences: SharedPreferences,
                private val hatenaBookmarkService: HatenaBookmarkService) {

    companion object {
        private const val KEY_PREF_USER = "KEY_PREF_USER"
    }

    private val isLoadingSubject = BehaviorSubject.create<Boolean>()
    private val userSubject = BehaviorSubject.create<UserEntity>()
    private val unauthorisedSubject = BehaviorSubject.create<Unit>()

    private val isRaisedErrorSubject = PublishSubject.create<Unit>()

    val user: Observable<UserEntity> = userSubject
    val isLoading: Observable<Boolean> = isLoadingSubject
    val unauthorised: Observable<Unit> = unauthorisedSubject

    val isRaisedError: Observable<Unit> = isRaisedErrorSubject

    init {
        userSubject.onNext(getUserFromPreferences())
        userSubject.doAfterNext {
            storeUserToPreferences(it)
        }
    }

    fun setUpUserId(userId: String) {

        isLoadingSubject.onNext(true)

        hatenaBookmarkService.userCheck(userId).map {
            // 特定の記号が入っている場合にTopページを取得しているケースがある.
            // 取り急ぎModelの仕様としてはトップページが返ってきたら 存在しないユーザー = 404として扱う
            return@map !it.contains("<title>はてなブックマーク</title>")
        }.onErrorResumeNext {
            if (it is HttpException) {
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
                userSubject.onNext(UserEntity(userId))
            } else {
                unauthorisedSubject.onNext(Unit)
            }
        }, {
            isRaisedErrorSubject.onNext(Unit)
        }, {
            isLoadingSubject.onNext(false)
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
