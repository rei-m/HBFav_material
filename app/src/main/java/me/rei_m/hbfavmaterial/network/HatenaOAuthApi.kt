package me.rei_m.hbfavmaterial.network

import me.rei_m.hbfavmaterial.entities.OAuthTokenEntity
import me.rei_m.hbfavmaterial.exeptions.HTTPException
import oauth.signpost.basic.DefaultOAuthConsumer
import oauth.signpost.basic.DefaultOAuthProvider
import rx.Observable
import java.net.HttpURLConnection
import java.net.URL

public class HatenaOAuthApi(consumerKey: String, consumerSecret: String) {

    private val mOAuthConsumer = DefaultOAuthConsumer(consumerKey, consumerSecret)

    private val mOAuthProvider = DefaultOAuthProvider(REQUEST_TOKEN_ENDPOINT_URL,
            ACCESS_TOKEN_ENDPOINT_URL,
            AUTHORIZATION_WEBSITE_URL)

    companion object {

        public final val CALLBACK = "https://github.com/rei-m/HBFav_material"

        private final val REQUEST_TOKEN_ENDPOINT_URL = "https://www.hatena.com/oauth/initiate?scope=read_public,write_public"

        private final val ACCESS_TOKEN_ENDPOINT_URL = "https://www.hatena.com/oauth/token"

        private final val AUTHORIZATION_WEBSITE_URL = "https://www.hatena.ne.jp/touch/oauth/authorize"
    }

    public fun requestRequestToken(): Observable<String> {

        return Observable.create({ t ->

            val authUrl = mOAuthProvider.retrieveRequestToken(mOAuthConsumer, CALLBACK)

            if (authUrl != null) {
                t.onNext(authUrl)
            } else {
                t.onError(HTTPException(HttpURLConnection.HTTP_BAD_REQUEST))
            }

            t.onCompleted()
        })

    }

    public fun requestAccessToken(requestToken: String): Observable<OAuthTokenEntity> {

        return Observable.create({ t ->

            mOAuthProvider.retrieveAccessToken(mOAuthConsumer, requestToken)

            if (mOAuthConsumer.token != null && mOAuthConsumer.tokenSecret != null) {
                t.onNext(OAuthTokenEntity(mOAuthConsumer.token, mOAuthConsumer.tokenSecret))
            } else {
                t.onError(HTTPException(HttpURLConnection.HTTP_BAD_REQUEST))
            }

            t.onCompleted()
        })

    }

    public fun getBookmark(oauthToken: OAuthTokenEntity, urlString: String): Observable<String> {

        mOAuthConsumer.setTokenWithSecret(oauthToken.token, oauthToken.secretToken)

        return Observable.create({ t ->

            val url = URL("http://api.b.hatena.ne.jp/1/my/bookmark?url=$urlString")
            val request = url.openConnection() as HttpURLConnection

            mOAuthConsumer.sign(request)

            request.connect()

            println(request.responseCode)
            val inputStream = request.inputStream
            val bodyByte = ByteArray(1024)
            inputStream.read(bodyByte)
            inputStream.close()
            println(String(bodyByte))

            t.onNext("")

            t.onCompleted()
        })

    }
}