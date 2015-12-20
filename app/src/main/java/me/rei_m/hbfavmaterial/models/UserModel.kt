package me.rei_m.hbfavmaterial.models

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import me.rei_m.hbfavmaterial.entities.UserEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.ui.UserIdCheckedEvent
import me.rei_m.hbfavmaterial.extensions.getAppPreferences
import me.rei_m.hbfavmaterial.network.UserCheckRequest
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * ユーザー情報を管理するModel.
 */
public class UserModel {

    public var isBusy = false
        private set

    public var userEntity: UserEntity? = null
        private set

    companion object {
        private final val KEY_PREF_USER = "KEY_PREF_USER"
    }

    /**
     * コンストラクタ.
     */
    constructor(context: Context) {
        // Preferencesに保存しているユーザー情報を復元する.
        val pref = getPreferences(context)
        val userJsonString = pref.getString(KEY_PREF_USER, null)
        if (userJsonString != null) {
            userEntity = Gson().fromJson(userJsonString, UserEntity::class.java)
        }
    }

    /**
     * ユーザー情報が設定済か判定する.
     */
    public fun isSetUserSetting(): Boolean {
        return userEntity != null
    }

    /**
     * 指定されたユーザーIDの有効確認と保存を行う.
     */
    public fun checkAndSaveUserId(context: Context, id: String) {

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
                    saveUser(context)
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

        UserCheckRequest.request(id)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo { isBusy = false }
                .subscribe(observer)
    }

    /**
     * ユーザー情報をPreferencesに保存する.
     */
    private fun saveUser(context: Context) {
        getPreferences(context)
                .edit()
                .putString(KEY_PREF_USER, Gson().toJson(userEntity))
                .apply()
    }

    /**
     * ユーザー情報を削除する.
     */
    public fun deleteUser(context: Context) {
        userEntity = null
        getPreferences(context).edit().remove(KEY_PREF_USER)
    }

    /**
     * Preferencesを取得する.
     */
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getAppPreferences(UserModel::class.java.simpleName)
    }
}
