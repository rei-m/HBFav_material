package me.rei_m.hbfavmaterial.models

import android.content.Context
import me.rei_m.hbfavmaterial.entities.UserEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.ui.UserIdCheckedEvent
import me.rei_m.hbfavmaterial.repositories.UserRepository
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * ユーザー情報を管理するModel.
 */
class UserModel {

    private val userRepository = UserRepository()

    var isBusy = false
        private set

    var userEntity: UserEntity? = null
        private set

    /**
     * コンストラクタ.
     */
    constructor(context: Context) {
        // 端末に保存しているユーザー情報を復元する.
        userEntity = userRepository.find(context)
    }

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
                if (isSuccess) {
                    EventBusHolder.EVENT_BUS.post(UserIdCheckedEvent(UserIdCheckedEvent.Companion.Type.OK))
                } else {
                    EventBusHolder.EVENT_BUS.post(UserIdCheckedEvent(UserIdCheckedEvent.Companion.Type.NG))
                }
            }

            override fun onError(e: Throwable?) {
                EventBusHolder.EVENT_BUS.post(UserIdCheckedEvent(UserIdCheckedEvent.Companion.Type.ERROR))
            }
        }

        userRepository.checkId(id)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo { isBusy = false }
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
