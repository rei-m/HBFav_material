package me.rei_m.hbfavmaterial.network

import com.google.gson.Gson
import com.squareup.okhttp.CacheControl
import com.squareup.okhttp.HttpUrl
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.exeptions.HTTPException
import me.rei_m.hbfavmaterial.utils.ApiUtil
import rx.Observable
import java.io.Serializable
import java.net.HttpURLConnection

public final class EntryApi private constructor() {

    companion object {
        public fun request(entryUrl: String): Observable<BookmarkEntity> {

            val url = HttpUrl.Builder()
                    .scheme("http")
                    .host("b.hatena.ne.jp")
                    .addPathSegment("entry")
                    .addPathSegment("jsonlite")
                    .build()

            return Observable.create({ t ->

                val request = Request.Builder()
                        .url("${url.toString()}/$entryUrl")
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .build()

                val response = OkHttpClient().newCall(request).execute()

                if (response.code() == HttpURLConnection.HTTP_OK) {

                    val responseJson = Gson().fromJson(response.body().string(), Response::class.java)

                    Observable.from(responseJson.bookmarks).forEach { v ->
                        t.onNext(BookmarkEntity(
                                "",
                                entryUrl,
                                v.comment,
                                v.user,
                                ApiUtil.parseStringToDate(v.timestamp),
                                responseJson.count,
                                "",
                                "",
                                "",
                                "",
                                v.tags
                        ))
                    }
                } else {
                    t.onError(HTTPException(response.code()))
                }

                t.onCompleted()
            })
        }

        private data class Response(val count: Int,
                                    val bookmarks: List<Entity>) : Serializable


        private data class Entity(val timestamp: String,
                                  val comment: String,
                                  val user: String,
                                  val tags: List<String>) : Serializable
    }
}