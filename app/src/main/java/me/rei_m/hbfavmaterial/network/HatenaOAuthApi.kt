package me.rei_m.hbfavmaterial.network

import com.google.gson.Gson
import me.rei_m.hbfavmaterial.entities.HatenaRestApiBookmarkResponse
import me.rei_m.hbfavmaterial.entities.OAuthTokenEntity
import me.rei_m.hbfavmaterial.exeptions.HTTPException
import oauth.signpost.basic.DefaultOAuthConsumer
import oauth.signpost.basic.DefaultOAuthProvider
import rx.Observable
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

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

        private final val BOOKMARK_ENDPOINT_URL = "http://api.b.hatena.ne.jp/1/my/bookmark"

        public final val AUTHORIZATION_DENY_URL = "$AUTHORIZATION_WEBSITE_URL.deny"

        private final val TWO_HYPHEN = "--"
        private final val EOL = "\r\n"
        private final val BOUNDARY = Random().hashCode()
        private final val CHARSET = "UTF-8"
    }

    public fun requestRequestToken(): Observable<String> {

        return Observable.create({ t ->

            val authUrl = mOAuthProvider.retrieveRequestToken(mOAuthConsumer, CALLBACK)

            if (authUrl != null) {
                t.onNext(authUrl)
            } else {
                t.onError(HTTPException(HttpURLConnection.HTTP_NOT_AUTHORITATIVE))
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
                t.onError(HTTPException(HttpURLConnection.HTTP_NOT_AUTHORITATIVE))
            }

            t.onCompleted()
        })

    }

    public fun getBookmark(oauthToken: OAuthTokenEntity, urlString: String):
            Observable<HatenaRestApiBookmarkResponse> {

        mOAuthConsumer.setTokenWithSecret(oauthToken.token, oauthToken.secretToken)

        return Observable.create({ t ->

            val url = URL("$BOOKMARK_ENDPOINT_URL?url=$urlString")
            val connection = url.openConnection() as HttpURLConnection

            mOAuthConsumer.sign(connection)

            connection.connect()

            when (connection.responseCode) {
                HttpURLConnection.HTTP_OK -> {
                    val response = Gson().fromJson(readStream(connection.inputStream), HatenaRestApiBookmarkResponse::class.java)
                    connection.disconnect()
                    t.onNext(response)
                }
                else -> {
                    connection.disconnect()
                    t.onError(HTTPException(connection.responseCode))
                }
            }

            t.onCompleted()
        })
    }

    public fun postBookmark(oauthToken: OAuthTokenEntity, urlString: String, comment: String, isOpen: Boolean):
            Observable<HatenaRestApiBookmarkResponse> {

        mOAuthConsumer.setTokenWithSecret(oauthToken.token, oauthToken.secretToken)

        return Observable.create({ t ->

            // Content作成開始
            val sb = StringBuilder()

            sb.append(EOL)

            // multipart/form-dataでパラメータ作成
            sb.append(createFormDataParameter("url", urlString))
            sb.append(createFormDataParameter("comment", comment))
            sb.append(createFormDataParameter("private", if (isOpen) "0" else "1"))

            // RequestHeaderに設定するためPostデータのLengthを取得
            var contentLength = sb.toString().toByteArray(CHARSET).size

            // Postデータにフッタ追加
            sb.append("$TWO_HYPHEN$BOUNDARY$TWO_HYPHEN$EOL")

            // コネクション取得
            val connection = createPostConnection(BOOKMARK_ENDPOINT_URL, contentLength)

            // OAuth認証
            mOAuthConsumer.sign(connection)

            // Postデータ書き込み
            val os = DataOutputStream(connection.outputStream)
            os.write(sb.toString().toByteArray(CHARSET))
            os.flush()
            os.close()

            when (connection.responseCode) {
                HttpURLConnection.HTTP_OK -> {
                    val response = Gson().fromJson(readStream(connection.inputStream), HatenaRestApiBookmarkResponse::class.java)
                    connection.disconnect()
                    t.onNext(response)
                }
                else -> {
                    connection.disconnect()
                    t.onError(HTTPException(connection.responseCode))
                }
            }

            t.onCompleted()
        })
    }

    public fun deleteBookmark(oauthToken: OAuthTokenEntity, urlString: String):
            Observable<String> {

        mOAuthConsumer.setTokenWithSecret(oauthToken.token, oauthToken.secretToken)

        return Observable.create({ t ->

            val url = URL("$BOOKMARK_ENDPOINT_URL?url=$urlString")
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "DELETE"

            mOAuthConsumer.sign(connection)

            connection.connect()

            when (connection.responseCode) {
                HttpURLConnection.HTTP_NO_CONTENT -> {
                    connection.disconnect()
                    t.onNext("")
                }
                else -> {
                    connection.disconnect()
                    t.onError(HTTPException(connection.responseCode))
                }
            }

            t.onCompleted()
        })
    }

    private fun createFormDataParameter(key: String, value: String): String {
        val sb = StringBuilder()
        sb.append("$TWO_HYPHEN$BOUNDARY$EOL")
                .append("Content-Disposition: form-data; name=\"$key\"$EOL")
                .append(EOL)
                .append("$value$EOL")

        return sb.toString()
    }

    private fun createPostConnection(urlString: String, contentLength: Int): HttpURLConnection {

        val url = URL(urlString)

        val connection = url.openConnection() as HttpURLConnection

        connection.requestMethod = "POST"
        connection.doInput = true
        connection.doOutput = true
        connection.readTimeout = 10 * 1000
        connection.connectTimeout = 10 * 1000
        connection.useCaches = false
        connection.setChunkedStreamingMode(0)

        connection.addRequestProperty("Connection", "Keep-Alive")
        connection.addRequestProperty("Content-Type", "multipart/form-data; boundary=$BOUNDARY")
        connection.addRequestProperty("Content-Length", contentLength.toString())

        return connection
    }

    private fun readStream(stream: InputStream): String {

        val sb = StringBuilder()

        val br = BufferedReader(InputStreamReader(stream))

        var line = br.readLine()
        while (line != null) {
            sb.append(line)
            line = br.readLine()
        }

        stream.close()

        return sb.toString()
    }
}