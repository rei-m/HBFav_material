package me.rei_m.hbfavmaterial.domain.service.impl

import android.app.Activity
import android.content.Intent
import com.twitter.sdk.android.Twitter
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import me.rei_m.hbfavmaterial.domain.entity.OAuthTokenEntity
import me.rei_m.hbfavmaterial.domain.entity.TwitterSessionEntity
import me.rei_m.hbfavmaterial.domain.repository.TwitterSessionRepository
import me.rei_m.hbfavmaterial.domain.service.TwitterService

class TwitterServiceImpl(val twitterSessionRepository: TwitterSessionRepository) : TwitterService {

    init {
        val twitterSessionEntity = twitterSessionRepository.resolve()
        if (twitterSessionEntity.oAuthTokenEntity.isAuthorised) {
            val token = TwitterAuthToken(twitterSessionEntity.oAuthTokenEntity.token, twitterSessionEntity.oAuthTokenEntity.secretToken)
            Twitter.getSessionManager().activeSession = TwitterSession(token, twitterSessionEntity.userId, twitterSessionEntity.userName)
        }
    }

    override fun authorize(activity: Activity) {
        TwitterAuthClient().authorize(activity, object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>?) {
                val twitterSession = result?.data ?: return
                val twitterSessionEntity = TwitterSessionEntity(
                        userId = twitterSession.userId,
                        userName = twitterSession.userName,
                        oAuthTokenEntity = OAuthTokenEntity(
                                token = twitterSession.authToken.token,
                                secretToken = twitterSession.authToken.secret
                        )
                )
                twitterSessionRepository.store(twitterSessionEntity)
            }

            override fun failure(exception: TwitterException?) {
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        TwitterAuthClient().onActivityResult(requestCode, resultCode, data)
    }

    override fun postTweet(text: String) {
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
}
