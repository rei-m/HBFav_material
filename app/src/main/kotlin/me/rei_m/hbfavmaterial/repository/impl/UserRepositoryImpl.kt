package me.rei_m.hbfavmaterial.repository.impl

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import me.rei_m.hbfavmaterial.entity.UserEntity
import me.rei_m.hbfavmaterial.extension.getAppPreferences
import me.rei_m.hbfavmaterial.repository.UserRepository

class UserRepositoryImpl(context: Context) : UserRepository {

    companion object {
        private const val KEY_PREF_USER = "KEY_PREF_USER"
    }

    private var userEntity: UserEntity

    init {
        val pref = getPreferences(context)
        val userJsonString = pref.getString(KEY_PREF_USER, null)
        userEntity = if (userJsonString != null) {
            Gson().fromJson(userJsonString, UserEntity::class.java)
        } else {
            UserEntity(id = "")
        }
    }

    override fun resolve(): UserEntity = userEntity

    override fun store(context: Context, userEntity: UserEntity) {
        getPreferences(context)
                .edit()
                .putString(KEY_PREF_USER, Gson().toJson(userEntity))
                .apply()
        this.userEntity = userEntity
    }

    override fun delete(context: Context) {
        getPreferences(context).edit().remove(KEY_PREF_USER).apply()
        this.userEntity = UserEntity(id = "")
    }

    private fun getPreferences(context: Context): SharedPreferences {
        // UserModelから移行したのでキーはそのまま.
        return context.getAppPreferences("UserModel")
    }
}
