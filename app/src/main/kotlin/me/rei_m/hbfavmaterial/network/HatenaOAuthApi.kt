package me.rei_m.hbfavmaterial.network

import me.rei_m.hbfavmaterial.entities.OAuthTokenEntity
import me.rei_m.hbfavmaterial.exeptions.HTTPException
import me.rei_m.hbfavmaterial.network.response.HatenaRestApiBookmarkResponse
import rx.Observable
import java.net.HttpURLConnection
import java.util.*

/**
 * はてなのOAuth認証関連のAPIを管理するクラス.
 */
class HatenaOAuthApi(consumerKey: String, consumerSecret: String) {

    private val hatenaOAuthManager = HatenaOAuthManager(consumerKey, consumerSecret)

    companion object {

        val CALLBACK = "https://github.com/rei-m/HBFav_material"
        
        private val AUTHORIZATION_WEBSITE_URL = "https://www.hatena.ne.jp/touch/oauth/authorize"

        val AUTHORIZATION_DENY_URL = "$AUTHORIZATION_WEBSITE_URL.deny"
    }

    /**
     * リクエストトークンを取得する.
     */
    fun requestRequestToken(): Observable<String> {

        return Observable.create { t ->

            val authUrl = hatenaOAuthManager.retrieveRequestToken()

            if (authUrl != null) {
                t.onNext(authUrl)
                t.onCompleted()
            } else {
                t.onError(HTTPException(HttpURLConnection.HTTP_UNAUTHORIZED))
            }
        }
    }

    /**
     * アクセストークンを取得する.
     */
    fun requestAccessToken(requestToken: String): Observable<OAuthTokenEntity> {

        return Observable.create { t ->

            hatenaOAuthManager.retrieveAccessToken(requestToken)

            if (hatenaOAuthManager.hasToken) {
                t.onNext(OAuthTokenEntity(hatenaOAuthManager.consumer.token, hatenaOAuthManager.consumer.tokenSecret))
                t.onCompleted()
            } else {
                t.onError(HTTPException(HttpURLConnection.HTTP_UNAUTHORIZED))
            }
        }
    }

    /**
     * 指定されたURLのブックマーク情報を取得する.
     */
    fun getBookmark(oauthToken: OAuthTokenEntity,
                    urlString: String): Observable<HatenaRestApiBookmarkResponse> {

        hatenaOAuthManager.consumer.setTokenWithSecret(oauthToken.token, oauthToken.secretToken)

        val retrofit = RetrofitManager.oauthRetrofitFactory(hatenaOAuthManager.consumer)

        return retrofit.create(HatenaOAuthApiService::class.java)
                .getBookmark(urlString)
    }

    /**
     * ブックマーク情報を更新する.
     */
    fun postBookmark(oauthToken: OAuthTokenEntity,
                     urlString: String,
                     comment: String,
                     isOpen: Boolean,
                     tags: List<String> = ArrayList<String>()): Observable<HatenaRestApiBookmarkResponse> {

        hatenaOAuthManager.consumer.setTokenWithSecret(oauthToken.token, oauthToken.secretToken)

        val retrofit = RetrofitManager.oauthRetrofitFactory(hatenaOAuthManager.consumer)

        return retrofit.create(HatenaOAuthApiService::class.java)
                .postBookmark(urlString,
                        comment,
                        if (isOpen) "0" else "1",
                        tags.toTypedArray())
    }

    /**
     * ブックマーク情報を削除する.
     */
    fun deleteBookmark(oauthToken: OAuthTokenEntity, urlString: String): Observable<Void?> {

        hatenaOAuthManager.consumer.setTokenWithSecret(oauthToken.token, oauthToken.secretToken)

        val retrofit = RetrofitManager.oauthRetrofitFactory(hatenaOAuthManager.consumer)

        return retrofit.create(HatenaOAuthApiService::class.java)
                .deleteBookmark(urlString)
    }
}
