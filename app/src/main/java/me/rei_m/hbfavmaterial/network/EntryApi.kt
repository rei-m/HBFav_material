package me.rei_m.hbfavmaterial.network

import com.google.gson.Gson
import com.squareup.okhttp.CacheControl
import com.squareup.okhttp.HttpUrl
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import me.rei_m.hbfavmaterial.exeptions.HTTPException
import rx.Observable
import java.io.Serializable
import java.net.HttpURLConnection

/**
 * 記事に紐づくブックマーク情報をAPIから取得するクラス.
 */
class EntryApi {

    companion object {

        private val url = HttpUrl.Builder()
                .scheme("http")
                .host("b.hatena.ne.jp")
                .addPathSegment("entry")
                .addPathSegment("jsonlite")
                .build()

        data class Response(val count: Int,
                            val bookmarks: List<Entity>) : Serializable

        data class Entity(val timestamp: String,
                          val comment: String,
                          val user: String,
                          val tags: List<String>) : Serializable
    }

    fun request(entryUrl: String): Observable<Response> {

        return Observable.create { t ->

            val request = Request.Builder()
                    .url("${url.toString()}/$entryUrl")
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build()

            val response = OkHttpClient().newCall(request).execute()
            if (response.code() == HttpURLConnection.HTTP_OK) {
                t.onNext(Gson().fromJson(response.body().string(), Response::class.java))
            } else {
                t.onError(HTTPException(response.code()))
            }

            t.onCompleted()
        }
    }
}
