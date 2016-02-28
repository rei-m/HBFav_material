package me.rei_m.hbfavmaterial.network

import com.google.gson.Gson
import me.rei_m.hbfavmaterial.exeptions.HTTPException
import okhttp3.CacheControl
import okhttp3.HttpUrl
import okhttp3.Request
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

            val response = HttpClient.instance.newCall(request).execute()
            if (response.code() == HttpURLConnection.HTTP_OK) {
                t.onNext(Gson().fromJson(response.body().string(), Response::class.java))
                t.onCompleted()
            } else {
                t.onError(HTTPException(response.code()))
            }
        }
    }
}
