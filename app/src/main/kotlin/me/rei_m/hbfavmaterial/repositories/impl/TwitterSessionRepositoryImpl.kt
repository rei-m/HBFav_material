package me.rei_m.hbfavmaterial.repositories.impl

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.twitter.sdk.android.Twitter
import com.twitter.sdk.android.core.TwitterAuthToken
import com.twitter.sdk.android.core.TwitterSession
import me.rei_m.hbfavmaterial.entities.TwitterSessionEntity
import me.rei_m.hbfavmaterial.extensions.getAppPreferences
import me.rei_m.hbfavmaterial.repositories.TwitterSessionRepository

class TwitterSessionRepositoryImpl(context: Context) : TwitterSessionRepository {

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

    override fun store(context: Context, twitterSessionEntity: TwitterSessionEntity) {
        getPreferences(context)
                .edit()
                .putString(KEY_PREF_TWITTER_SESSION, Gson().toJson(twitterSessionEntity))
                .putBoolean(KEY_PREF_TWITTER_SESSION, twitterSessionEntity.isShare)
                .apply()
        this.twitterSessionEntity = twitterSessionEntity
    }

    override fun delete(context: Context) {
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
