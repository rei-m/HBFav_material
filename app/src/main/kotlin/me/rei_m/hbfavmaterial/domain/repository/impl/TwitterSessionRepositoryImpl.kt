package me.rei_m.hbfavmaterial.domain.repository.impl

import android.content.SharedPreferences
import com.google.gson.Gson
import me.rei_m.hbfavmaterial.domain.entity.TwitterSessionEntity
import me.rei_m.hbfavmaterial.domain.repository.TwitterSessionRepository

class TwitterSessionRepositoryImpl(private val preferences: SharedPreferences) : TwitterSessionRepository {

    companion object {
        private const val KEY_PREF_TWITTER_SESSION = "KEY_PREF_TWITTER_SESSION"
        private const val KEY_PREF_IS_SHARE_TWITTER = "KEY_PREF_IS_SHARE_TWITTER"
    }

    private var twitterSessionEntity: TwitterSessionEntity

    init {
        val twitterSessionJsonString = preferences.getString(KEY_PREF_TWITTER_SESSION, null)
        twitterSessionEntity = if (twitterSessionJsonString != null) {
            Gson().fromJson(twitterSessionJsonString, TwitterSessionEntity::class.java)
        } else {
            TwitterSessionEntity()
        }
        val isShare = preferences.getBoolean(KEY_PREF_IS_SHARE_TWITTER, false)
        twitterSessionEntity.isShare = isShare
    }

    override fun resolve(): TwitterSessionEntity = twitterSessionEntity

    override fun store(twitterSessionEntity: TwitterSessionEntity) {
        preferences.edit()
                .putString(KEY_PREF_TWITTER_SESSION, Gson().toJson(twitterSessionEntity))
                .putBoolean(KEY_PREF_IS_SHARE_TWITTER, twitterSessionEntity.isShare)
                .apply()
        this.twitterSessionEntity = twitterSessionEntity
    }

    override fun delete() {
        preferences.edit()
                .remove(KEY_PREF_TWITTER_SESSION)
                .remove(KEY_PREF_IS_SHARE_TWITTER)
                .apply()
        this.twitterSessionEntity = TwitterSessionEntity()
    }
}
