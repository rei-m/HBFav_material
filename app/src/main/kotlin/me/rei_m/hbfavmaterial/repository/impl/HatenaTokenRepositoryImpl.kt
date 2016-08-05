package me.rei_m.hbfavmaterial.repository.impl

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import me.rei_m.hbfavmaterial.entitiy.OAuthTokenEntity
import me.rei_m.hbfavmaterial.extension.getAppPreferences
import me.rei_m.hbfavmaterial.repository.HatenaTokenRepository

class HatenaTokenRepositoryImpl(context: Context) : HatenaTokenRepository {

    companion object {
        private const val KEY_PREF_OAUTH = "KEY_PREF_OAUTH"
    }

    private var oAuthTokenEntity: OAuthTokenEntity

    init {
        val pref = getPreferences(context)
        val oauthJsonString = pref.getString(KEY_PREF_OAUTH, null)
        oAuthTokenEntity = if (oauthJsonString != null) {
            Gson().fromJson(oauthJsonString, OAuthTokenEntity::class.java)
        } else {
            OAuthTokenEntity()
        }
    }

    override fun resolve(): OAuthTokenEntity = oAuthTokenEntity

    override fun store(context: Context, oAuthTokenEntity: OAuthTokenEntity) {
        getPreferences(context)
                .edit()
                .putString(KEY_PREF_OAUTH, Gson().toJson(oAuthTokenEntity))
                .apply()
        this.oAuthTokenEntity = oAuthTokenEntity
    }

    override fun delete(context: Context) {
        getPreferences(context).edit().remove(KEY_PREF_OAUTH).apply()
        this.oAuthTokenEntity = OAuthTokenEntity()
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getAppPreferences("HatenaModel")
    }
}