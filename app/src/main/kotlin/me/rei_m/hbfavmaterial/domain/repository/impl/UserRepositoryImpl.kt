package me.rei_m.hbfavmaterial.domain.repository.impl

import android.content.SharedPreferences
import com.google.gson.Gson
import me.rei_m.hbfavmaterial.domain.entity.UserEntity
import me.rei_m.hbfavmaterial.domain.repository.UserRepository

class UserRepositoryImpl(private val preferences: SharedPreferences) : UserRepository {

    companion object {
        private const val KEY_PREF_USER = "KEY_PREF_USER"
    }

    private var userEntity: UserEntity

    init {
        val userJsonString = preferences.getString(KEY_PREF_USER, null)
        userEntity = if (userJsonString != null) {
            Gson().fromJson(userJsonString, UserEntity::class.java)
        } else {
            UserEntity(id = "")
        }
    }

    override fun resolve(): UserEntity = userEntity

    override fun store(userEntity: UserEntity) {
        preferences.edit()
                .putString(KEY_PREF_USER, Gson().toJson(userEntity))
                .apply()
        this.userEntity = userEntity
    }

    override fun delete() {
        preferences.edit().remove(KEY_PREF_USER).apply()
        this.userEntity = UserEntity(id = "")
    }
}
