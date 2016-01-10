package me.rei_m.hbfavmaterial.models

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.google.gson.Gson
import com.twitter.sdk.android.Twitter
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import com.twitter.sdk.android.core.models.Tweet
import me.rei_m.hbfavmaterial.entities.OAuthTokenEntity
import me.rei_m.hbfavmaterial.entities.TwitterSessionEntity
import me.rei_m.hbfavmaterial.extensions.getAppPreferences

/**
 * Twitter関連の情報を管理するModel.
 */
class TwitterModel {

    public var isBusy = false
        private set

    public var twitterSessionEntity: TwitterSessionEntity? = null
        private set

    public var isShare: Boolean = false
        private set

    companion object {
        private val KEY_PREF_TWITTER_SESSION = "KEY_PREF_TWITTER_SESSION"
        private val KEY_PREF_IS_SHARE_TWITTER = "KEY_PREF_IS_SHARE_TWITTER"
    }

    /**
     * コンストラクタ.
     */
    constructor(context: Context) {

        // Preferencesからアクセストークンを復元する.
        val pref = getPreferences(context)
        val twitterSessionJsonString = pref.getString(KEY_PREF_TWITTER_SESSION, null)
        if (twitterSessionJsonString != null) {
            twitterSessionEntity = Gson().fromJson(twitterSessionJsonString, TwitterSessionEntity::class.java)
            twitterSessionEntity?.apply {
                val token = TwitterAuthToken(oAuthTokenEntity.token, oAuthTokenEntity.secretToken)
                Twitter.getSessionManager().activeSession = TwitterSession(token, userId, userName)
            }
        }
        isShare = pref.getBoolean(KEY_PREF_IS_SHARE_TWITTER, false)
    }

    /**
     * OAuth認証済か判定する.
     */
    public fun isAuthorised(): Boolean {
        return Twitter.getSessionManager().activeSession != null
    }

    /**
     * OAuth認証を行う.
     */
    public fun authorize(activity: Activity) {

        if (isBusy) {
            return
        }

        isBusy = true

        TwitterAuthClient().authorize(activity, object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>?) {
                result ?: return
                saveSession(activity.applicationContext, result.data)
            }

            override fun failure(exception: TwitterException?) {
            }
        })
    }

    /**
     * OAuth認証後のコールバック処理を行う.
     */
    public fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        isBusy = false
        TwitterAuthClient().onActivityResult(requestCode, resultCode, data)
    }

    /**
     * Busyフラグをクリアする.
     */
    public fun clearBusy() {
        isBusy = false
    }

    /**
     * ツイートを投稿する.
     */
    public fun postTweet(text: String) {

        if (isBusy) {
            return
        }

        isBusy = true

        Twitter.getApiClient().statusesService.update(text,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                null,
                object : Callback<Tweet>() {
                    override fun success(result: Result<Tweet>?) {
                        isBusy = false
                        result ?: return
                    }

                    override fun failure(exception: TwitterException?) {
                        isBusy = false
                    }
                })
    }

    public fun setIsShare(context: Context, value: Boolean) {
        isShare = value
        saveIsShare(context)
    }

    /**
     * セッションを保存する.
     */
    private fun saveSession(context: Context, twitterSession: TwitterSession) {

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

    private fun saveIsShare(context: Context) {
        getPreferences(context)
                .edit()
                .putBoolean(KEY_PREF_IS_SHARE_TWITTER, isShare)
                .apply()
    }

    /**
     * Preferencesを取得する
     */
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getAppPreferences(TwitterModel::class.java.simpleName)
    }
}
