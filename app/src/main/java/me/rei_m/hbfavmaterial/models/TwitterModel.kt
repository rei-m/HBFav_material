package me.rei_m.hbfavmaterial.models

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.twitter.sdk.android.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterSession
import io.fabric.sdk.android.Fabric
import me.rei_m.hbfavmaterial.entities.OAuthTokenEntity
import me.rei_m.hbfavmaterial.entities.TwitterSessionEntity
import me.rei_m.hbfavmaterial.extensions.getAppPreferences
import me.rei_m.hbfavmaterial.extensions.getAssetToJson

/**
 * はてなのOAuth関連の情報を管理するModel.
 */
public class TwitterModel {

    public var isBusy = false
        private set

    public var twitterSessionEntity: TwitterSessionEntity? = null
        private set

    companion object {
        private val KEY_PREF_TWITTER_SESSION = "KEY_PREF_TWITTER_SESSION"
    }

    /**
     * コンストラクタ.
     */
    constructor(context: Context) {

        // OAuth認証用のキーを作成し、OAuthAPIを作成する.
        val twitterJson = context.getAssetToJson("twitter.json")
        val authConfig = TwitterAuthConfig(twitterJson.getString("consumer_key"), twitterJson.getString("consumer_secret"))
        Fabric.with(context, Twitter(authConfig))

        //mHatenaOAuthApi = HatenaOAuthApi(hatenaJson.getString("consumer_key"), hatenaJson.getString("consumer_secret"))

        // Preferencesからアクセストークンを復元する.
        val pref = getPreferences(context)
        val twitterSessionJsonString = pref.getString(KEY_PREF_TWITTER_SESSION, null)
        if (twitterSessionJsonString != null) {
            twitterSessionEntity = Gson().fromJson(twitterSessionJsonString, TwitterSessionEntity::class.java)
        }
    }

    public fun saveSession(context: Context, twitterSession: TwitterSession) {

        twitterSessionEntity = TwitterSessionEntity(
                userId = twitterSession.userId,
                userName = twitterSession.userName,
                oAuthTokenEntity = OAuthTokenEntity(
                        token = twitterSession.authToken.token,
                        secretToken = twitterSession.authToken.secret
                )
        )
        saveSession(context)
    }

    /**
     * PreferencesにModel内のセッション情報を保存する.
     */
    private fun saveSession(context: Context) {
        getPreferences(context)
                .edit()
                .putString(KEY_PREF_TWITTER_SESSION, Gson().toJson(twitterSessionEntity))
                .apply()
    }

    /**
     * Preferencesを取得する
     */
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getAppPreferences(TwitterModel::class.java.simpleName)
    }
}
