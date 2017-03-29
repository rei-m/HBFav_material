package me.rei_m.hbfavmaterial.infra.network

import android.net.Uri
import oauth.signpost.OAuth
import oauth.signpost.http.HttpParameters
import okhttp3.HttpUrl
import okhttp3.Request
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer
import java.net.HttpURLConnection

class HatenaOAuthManager(private val consumerKey: String,
                         private val consumerSecret: String) {

    companion object {

        private const val REQUEST_TOKEN_ENDPOINT_URL = "https://www.hatena.com/oauth/initiate"

        private const val ACCESS_TOKEN_ENDPOINT_URL = "https://www.hatena.com/oauth/token"

        private const val AUTHORIZATION_WEBSITE_URL = "https://www.hatena.ne.jp/touch/oauth/authorize"

        const val AUTHORIZATION_DENY_URL = "https://www.hatena.ne.jp/oauth/authorize.deny"

        const val CALLBACK = "https://github.com/rei-m/HBFav_material"
    }

    var consumer = OkHttpOAuthConsumer(consumerKey, consumerSecret)
        private set

    fun retrieveRequestToken(): String? {

        consumer = OkHttpOAuthConsumer(consumerKey, consumerSecret)
        HttpParameters().apply {
            put("realm", "")
            put(OAuth.OAUTH_CALLBACK, Uri.encode(CALLBACK))
        }.let {
            consumer.setAdditionalParameters(it)
        }

        val authorizeUrl = HttpUrl.parse(REQUEST_TOKEN_ENDPOINT_URL)
                .newBuilder()
                .addQueryParameter("scope", "read_public,write_public")
                .build()

        val request = Request.Builder()
                .url(authorizeUrl)
                .build()

        val signedRequest = consumer.sign(request).unwrap() as Request

        val response = HttpClient.instance.newCall(signedRequest).execute()

        if (response.code() != HttpURLConnection.HTTP_OK) {
            return null
        }

        var oauthToken: String = ""
        var oauthTokenSecret: String = ""

        response.body().string().split("&").forEach {
            val param = it.split("=")
            when (param[0]) {
                OAuth.OAUTH_TOKEN -> oauthToken = Uri.decode(param[1])
                OAuth.OAUTH_TOKEN_SECRET -> oauthTokenSecret = Uri.decode(param[1])
            }
        }

        if (oauthToken.isBlank() || oauthTokenSecret.isBlank()) {
            return null
        }

        consumer.setTokenWithSecret(oauthToken, oauthTokenSecret)

        return HttpUrl.parse(AUTHORIZATION_WEBSITE_URL)
                .newBuilder()
                .addQueryParameter(OAuth.OAUTH_TOKEN, oauthToken)
                .build().toString()
    }

    fun retrieveAccessToken(requestToken: String): Boolean {

        var oauthToken = consumer.token

        var oauthTokenSecret = consumer.tokenSecret

        consumer = OkHttpOAuthConsumer(consumerKey, consumerSecret)

        consumer.setTokenWithSecret(oauthToken, oauthTokenSecret)

        HttpParameters().apply {
            put("realm", "")
            put(OAuth.OAUTH_VERIFIER, Uri.encode(requestToken))
        }.let {
            consumer.setAdditionalParameters(it)
        }

        val authorizeUrl = HttpUrl.parse(ACCESS_TOKEN_ENDPOINT_URL)
                .newBuilder()
                .build()

        val request = Request.Builder()
                .url(authorizeUrl)
                .build()

        val signedRequest = consumer.sign(request).unwrap() as Request

        val response = HttpClient.instance.newCall(signedRequest).execute()

        if (response.code() != HttpURLConnection.HTTP_OK) {
            consumer.setTokenWithSecret(null, null)
            return false
        }

        response.body().string().split("&").forEach {
            val param = it.split("=")
            when (param[0]) {
                OAuth.OAUTH_TOKEN -> oauthToken = Uri.decode(param[1])
                OAuth.OAUTH_TOKEN_SECRET -> oauthTokenSecret = Uri.decode(param[1])
            }
        }

        if (oauthToken.isBlank() || oauthTokenSecret.isBlank()) {
            consumer.setTokenWithSecret(null, null)
            return false
        }

        consumer.setTokenWithSecret(oauthToken, oauthTokenSecret)
        return true
    }
}
