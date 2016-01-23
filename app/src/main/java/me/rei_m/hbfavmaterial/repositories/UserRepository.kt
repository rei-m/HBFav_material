package me.rei_m.hbfavmaterial.repositories

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import me.rei_m.hbfavmaterial.entities.UserEntity
import me.rei_m.hbfavmaterial.extensions.getAppPreferences
import me.rei_m.hbfavmaterial.models.UserModel
import me.rei_m.hbfavmaterial.network.UserCheckRequest
import rx.Observable

open class UserRepository {

    companion object {
        private val KEY_PREF_USER = "KEY_PREF_USER"
    }

    /**
     * ユーザー情報を取得する.
     */
    open fun find(context: Context): UserEntity? {
        val pref = getPreferences(context)
        val userJsonString = pref.getString(KEY_PREF_USER, null)
        userJsonString ?: return null
        return Gson().fromJson(userJsonString, UserEntity::class.java)
    }

    /**
     * ユーザー情報を保存する.
     */
    open fun save(context: Context, userEntity: UserEntity) {
        getPreferences(context)
                .edit()
                .putString(KEY_PREF_USER, Gson().toJson(userEntity))
                .apply()
    }

    /**
     * ユーザー情報を削除する.
     */
    open fun delete(context: Context) {
        getPreferences(context).edit().remove(KEY_PREF_USER)
    }

    /**
     * ユーザーIDの有効性をチェックする.
     */
    open fun checkId(id: String): Observable<Boolean> {
        return UserCheckRequest().request(id)
    }

    /**
     * Preferencesを取得する.
     */
    private fun getPreferences(context: Context): SharedPreferences {
        // UserModelから移行したのでキーはそのまま.
        return context.getAppPreferences(UserModel::class.java.simpleName)
    }
}
