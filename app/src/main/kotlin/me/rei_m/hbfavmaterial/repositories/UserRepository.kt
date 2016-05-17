package me.rei_m.hbfavmaterial.repositories

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import me.rei_m.hbfavmaterial.entities.UserEntity
import me.rei_m.hbfavmaterial.extensions.getAppPreferences
import me.rei_m.hbfavmaterial.models.UserModel
import me.rei_m.hbfavmaterial.network.HatenaApiService
import me.rei_m.hbfavmaterial.network.RequestRetrofit
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
        return RequestRetrofit.newInstance().create(HatenaApiService::class.java).userCheck(id)
                .map {
                    // 原因はわからないがカンマ等の記号が入っている場合にTopページを取得しているケースがある
                    // 基本的には入力時に弾く予定だが、Modelの仕様としては考慮してトップページが返ってきたら
                    // 存在しないユーザー = 404として扱う
                    return@map !it.contains("<title>はてなブックマーク</title>")
                }
    }

    /**
     * Preferencesを取得する.
     */
    private fun getPreferences(context: Context): SharedPreferences {
        // UserModelから移行したのでキーはそのまま.
        return context.getAppPreferences(UserModel::class.java.simpleName)
    }
}
