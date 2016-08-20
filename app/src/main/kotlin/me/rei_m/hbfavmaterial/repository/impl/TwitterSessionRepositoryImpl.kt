package me.rei_m.hbfavmaterial.repository.impl

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import me.rei_m.hbfavmaterial.entity.TwitterSessionEntity
import me.rei_m.hbfavmaterial.extension.getAppPreferences
import me.rei_m.hbfavmaterial.repository.TwitterSessionRepository

class TwitterSessionRepositoryImpl(private val context: Context) : TwitterSessionRepository {

    companion object {
        private const val KEY_PREF_TWITTER_SESSION = "KEY_PREF_TWITTER_SESSION"
        private const val KEY_PREF_IS_SHARE_TWITTER = "KEY_PREF_IS_SHARE_TWITTER"
    }

    private var twitterSessionEntity: TwitterSessionEntity

    init {
        val pref = getPreferences(context)
        val twitterSessionJsonString = pref.getString(KEY_PREF_TWITTER_SESSION, null)
        twitterSessionEntity = if (twitterSessionJsonString != null) {
            Gson().fromJson(twitterSessionJsonString, TwitterSessionEntity::class.java)
        } else {
            TwitterSessionEntity()
        }
        val isShare = pref.getBoolean(KEY_PREF_IS_SHARE_TWITTER, false)
        twitterSessionEntity.isShare = isShare
    }

    override fun resolve(): TwitterSessionEntity = twitterSessionEntity

    override fun store(twitterSessionEntity: TwitterSessionEntity) {
        getPreferences(context)
                .edit()
                .putString(KEY_PREF_TWITTER_SESSION, Gson().toJson(twitterSessionEntity))
                .putBoolean(KEY_PREF_IS_SHARE_TWITTER, twitterSessionEntity.isShare)
                .apply()
        this.twitterSessionEntity = twitterSessionEntity
    }

    override fun delete() {
        getPreferences(context).edit()
                .remove(KEY_PREF_TWITTER_SESSION)
                .remove(KEY_PREF_IS_SHARE_TWITTER)
                .apply()
        this.twitterSessionEntity = TwitterSessionEntity()
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getAppPreferences("TwitterModel")
    }
}
