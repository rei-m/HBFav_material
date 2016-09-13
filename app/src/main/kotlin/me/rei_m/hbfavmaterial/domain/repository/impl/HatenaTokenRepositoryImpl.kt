package me.rei_m.hbfavmaterial.domain.repository.impl

import android.content.SharedPreferences
import com.google.gson.Gson
import me.rei_m.hbfavmaterial.domain.entity.OAuthTokenEntity
import me.rei_m.hbfavmaterial.domain.repository.HatenaTokenRepository

class HatenaTokenRepositoryImpl(private val preferences: SharedPreferences) : HatenaTokenRepository {

    companion object {
        private const val KEY_PREF_OAUTH = "KEY_PREF_OAUTH"
    }

    private var oAuthTokenEntity: OAuthTokenEntity

    init {
        val oauthJsonString = preferences.getString(KEY_PREF_OAUTH, null)
        oAuthTokenEntity = if (oauthJsonString != null) {
            Gson().fromJson(oauthJsonString, OAuthTokenEntity::class.java)
        } else {
            OAuthTokenEntity()
        }
    }

    override fun resolve(): OAuthTokenEntity = oAuthTokenEntity

    override fun store(oAuthTokenEntity: OAuthTokenEntity) {
        preferences.edit()
                .putString(KEY_PREF_OAUTH, Gson().toJson(oAuthTokenEntity))
                .apply()
        this.oAuthTokenEntity = oAuthTokenEntity
    }

    override fun delete() {
        preferences.edit().remove(KEY_PREF_OAUTH).apply()
        this.oAuthTokenEntity = OAuthTokenEntity()
    }
}
