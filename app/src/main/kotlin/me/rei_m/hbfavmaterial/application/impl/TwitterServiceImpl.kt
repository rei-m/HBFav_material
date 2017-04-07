package me.rei_m.hbfavmaterial.application.impl

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import com.google.gson.Gson
import com.twitter.sdk.android.Twitter
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.application.TwitterService
import me.rei_m.hbfavmaterial.model.entity.OAuthTokenEntity
import me.rei_m.hbfavmaterial.model.entity.TwitterSessionEntity

class TwitterServiceImpl(private val preferences: SharedPreferences) : TwitterService {

    companion object {
        private const val KEY_PREF_TWITTER_SESSION = "KEY_PREF_TWITTER_SESSION"
        private const val KEY_PREF_IS_SHARE_TWITTER = "KEY_PREF_IS_SHARE_TWITTER"
        private const val MAX_LENGTH_COMMENT_AT_TWITTER = 100
        private const val MAX_LENGTH_TITLE_WITH_COMMENT_AT_TWITTER = 10
        private const val MAX_LENGTH_TITLE_AT_TWITTER = MAX_LENGTH_COMMENT_AT_TWITTER + MAX_LENGTH_TITLE_WITH_COMMENT_AT_TWITTER
    }

    private var twitterSession: TwitterSessionEntity

    private val confirmAuthorisedEventSubject = PublishSubject.create<Boolean>()

    override val confirmAuthorisedEvent: Observable<Boolean> = confirmAuthorisedEventSubject

    init {
        val twitterSessionJsonString = preferences.getString(KEY_PREF_TWITTER_SESSION, null)
        val twitterSession = if (twitterSessionJsonString != null) {
            Gson().fromJson(twitterSessionJsonString, TwitterSessionEntity::class.java)
        } else {
            TwitterSessionEntity()
        }
        val isShare = preferences.getBoolean(KEY_PREF_IS_SHARE_TWITTER, false)
        twitterSession.isShare = isShare

        if (twitterSession.oAuthToken.isAuthorised) {
            val token = TwitterAuthToken(twitterSession.oAuthToken.token, twitterSession.oAuthToken.secretToken)
            Twitter.getSessionManager().activeSession = TwitterSession(token, twitterSession.userId, twitterSession.userName)
        }
        this.twitterSession = twitterSession
    }

    override fun confirmAuthorised() {
        confirmAuthorisedEventSubject.onNext(twitterSession.oAuthToken.isAuthorised)
    }

    override fun authorize(activity: Activity) {
        TwitterAuthClient().authorize(activity, object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>?) {
                val twitterSession = result?.data ?: return
                val twitterSessionEntity = TwitterSessionEntity(
                        userId = twitterSession.userId,
                        userName = twitterSession.userName,
                        oAuthToken = OAuthTokenEntity(
                                token = twitterSession.authToken.token,
                                secretToken = twitterSession.authToken.secret
                        )
                )
                preferences.edit()
                        .putString(KEY_PREF_TWITTER_SESSION, Gson().toJson(twitterSessionEntity))
                        .putBoolean(KEY_PREF_IS_SHARE_TWITTER, twitterSessionEntity.isShare)
                        .apply()
                this@TwitterServiceImpl.twitterSession = twitterSessionEntity
            }

            override fun failure(exception: TwitterException?) {
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        TwitterAuthClient().onActivityResult(requestCode, resultCode, data)
    }

    override fun postTweet(articleUrl: String, articleTitle: String, comment: String) {

        val text = createShareText(articleUrl, articleTitle, comment)

        Twitter.getApiClient().statusesService.update(text,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                null)
    }

    private fun createShareText(url: String, title: String, comment: String): String {
        return if (comment.isNotEmpty()) {
            val postComment: String
            val postTitle: String
            if (MAX_LENGTH_COMMENT_AT_TWITTER < comment.length) {
                postComment = comment.take(MAX_LENGTH_COMMENT_AT_TWITTER - 1) + "..."
                postTitle = if (MAX_LENGTH_TITLE_WITH_COMMENT_AT_TWITTER < (title.length)) {
                    title.take(MAX_LENGTH_TITLE_WITH_COMMENT_AT_TWITTER - 1) + "..."
                } else {
                    title
                }
            } else {
                postComment = comment
                val postTitleLength = MAX_LENGTH_TITLE_AT_TWITTER - comment.length
                postTitle = if (postTitleLength < title.length) {
                    title.take(postTitleLength - 1) + "..."
                } else {
                    title
                }
            }
            "$postComment \"$postTitle\" $url"
        } else {
            val postTitle = if (MAX_LENGTH_TITLE_AT_TWITTER < title.length) {
                title.substring(0, MAX_LENGTH_TITLE_AT_TWITTER - 1) + "..."
            } else {
                title
            }
            "\"$postTitle\" $url"
        }
    }
}
