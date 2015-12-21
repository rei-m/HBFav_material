package me.rei_m.hbfavmaterial.network

import com.google.gson.Gson
import com.squareup.okhttp.CacheControl
import com.squareup.okhttp.HttpUrl
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import me.rei_m.hbfavmaterial.entities.ArticleEntity
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.exeptions.HTTPException
import me.rei_m.hbfavmaterial.utils.ApiUtil
import rx.Observable
import java.io.Serializable
import java.net.HttpURLConnection

/**
 * 記事に紐づくブックマーク情報をAPIから取得するクラス.
 */
public class EntryApi {

    companion object {

        val url = HttpUrl.Builder()
                .scheme("http")
                .host("b.hatena.ne.jp")
                .addPathSegment("entry")
                .addPathSegment("jsonlite")
                .build()

        private data class Response(val count: Int,
                                    val bookmarks: List<Entity>) : Serializable

        private data class Entity(val timestamp: String,
                                  val comment: String,
                                  val user: String,
                                  val tags: List<String>) : Serializable
    }

    public fun request(entryUrl: String): Observable<BookmarkEntity> {

        return Observable.create { t ->

            val request = Request.Builder()
                    .url("${url.toString()}/$entryUrl")
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build()

            val response = OkHttpClient().newCall(request).execute()

            if (response.code() == HttpURLConnection.HTTP_OK) {

                val responseJson = Gson().fromJson(response.body().string(), Response::class.java)

                responseJson.bookmarks.forEach { v ->
                    val articleEntity = ArticleEntity(
                            "",
                            entryUrl,
                            responseJson.count,
                            "",
                            "",
                            "")

                    t.onNext(BookmarkEntity(
                            articleEntity,
                            v.comment,
                            v.user,
                            ApiUtil.parseStringToDate(v.timestamp),
                            "",
                            v.tags))
                }
            } else {
                t.onError(HTTPException(response.code()))
            }

            t.onCompleted()
        }
    }
}
