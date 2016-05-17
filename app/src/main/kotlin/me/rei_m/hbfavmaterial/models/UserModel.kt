package me.rei_m.hbfavmaterial.models

import android.content.Context
import me.rei_m.hbfavmaterial.di.ForApplication
import me.rei_m.hbfavmaterial.entities.UserEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.ui.UserIdCheckedEvent
import me.rei_m.hbfavmaterial.repositories.UserRepository
import retrofit2.adapter.rxjava.HttpException
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ユーザー情報を管理するModel.
 */
@Singleton
class UserModel @Inject constructor(@ForApplication context: Context, private val userRepository: UserRepository) {

    var isBusy = false
        private set

    var userEntity: UserEntity? = userRepository.find(context)
        private set
    
    /**
     * ユーザー情報が設定済か判定する.
     */
    fun isSetUserSetting(): Boolean {
        return userEntity != null
    }

    /**
     * 指定されたユーザーIDの有効確認と保存を行う.
     */
    fun checkAndSaveUserId(context: Context, id: String) {

        if (isBusy) {
            return
        }

        isBusy = true

        var isSuccess = false

        // ユーザーIDの存在を確認
        val observer = object : Observer<Boolean> {

            override fun onNext(t: Boolean?) {
                // 問題なければPreferenceに保存
                if (t!!) {
                    userEntity = UserEntity(id)
                    userRepository.save(context, userEntity!!);
                    isSuccess = true
                }
            }

            override fun onCompleted() {
                isBusy = false
                if (isSuccess) {
                    EventBusHolder.EVENT_BUS.post(UserIdCheckedEvent(UserIdCheckedEvent.Companion.Type.OK))
                } else {
                    EventBusHolder.EVENT_BUS.post(UserIdCheckedEvent(UserIdCheckedEvent.Companion.Type.NG))
                }
            }

            override fun onError(e: Throwable?) {
                isBusy = false
                if (e is HttpException) {
                    if (e.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                        EventBusHolder.EVENT_BUS.post(UserIdCheckedEvent(UserIdCheckedEvent.Companion.Type.NG))
                        return
                    }
                }
                EventBusHolder.EVENT_BUS.post(UserIdCheckedEvent(UserIdCheckedEvent.Companion.Type.ERROR))
            }
        }

        userRepository.checkId(id)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }

    /**
     * ユーザー情報を削除する.
     */
    fun deleteUser(context: Context) {
        userEntity = null
        userRepository.delete(context)
    }
}
