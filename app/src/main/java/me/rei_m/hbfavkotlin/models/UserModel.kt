package me.rei_m.hbfavkotlin.models

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import me.rei_m.hbfavkotlin.entities.UserEntity
import me.rei_m.hbfavkotlin.extensions.getAppPreferences

public class UserModel {

    public var userEntity: UserEntity? = null
        private set

    companion object {
        private final val KEY_PREF_USER = "KEY_PREF_USER"
    }

    constructor(context: Context) {
        val pref = getPreferences(context)
        val userJsonString = pref.getString(KEY_PREF_USER, null)
        if (userJsonString != null) {
            userEntity = Gson().fromJson(userJsonString, UserEntity::class.java)
        }
    }

    public fun isSetUserSetting(): Boolean {
        return userEntity != null
    }

    public fun checkAndSaveUserId(context: Context, id: String) {

        // ユーザーIDの存在を確認

        // 問題なければPreferenceに保存してイベント通知

        // いなければ見つからなかったイベント通知

    }

    public fun deleteUser(context: Context) {
        userEntity = null
        getPreferences(context).edit().remove(KEY_PREF_USER)
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getAppPreferences(UserModel::class.java.simpleName)
    }
}