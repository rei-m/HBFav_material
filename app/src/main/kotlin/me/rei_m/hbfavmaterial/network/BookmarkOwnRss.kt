package me.rei_m.hbfavmaterial.network

import me.rei_m.hbfavmaterial.enums.ReadAfterFilter
import me.rei_m.hbfavmaterial.exeptions.HTTPException
import okhttp3.CacheControl
import okhttp3.HttpUrl
import okhttp3.Request
import rx.Observable
import java.net.HttpURLConnection

/**
 * ユーザーのブックマークRSSを取得するクラス.
 */
class BookmarkOwnRss {

    fun request(userId: String, readAfterFilter: ReadAfterFilter, startIndex: Int = 0): Observable<String> {

        return Observable.create { t ->

            val builder = HttpUrl.Builder()
                    .scheme("http")
                    .host("b.hatena.ne.jp")
                    .addPathSegment(userId)
                    .addPathSegment("rss")
                    .addQueryParameter("of", startIndex.toString())

            if (readAfterFilter == ReadAfterFilter.AFTER_READ) {
                builder.addQueryParameter("tag", "あとで読む")
            }

            val request = Request.Builder()
                    .url(builder.build())
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build()

            val response = HttpClient.instance.newCall(request).execute()
            if (response.code() == HttpURLConnection.HTTP_OK) {
                t.onNext(response.body().string())
                t.onCompleted()
            } else {
                t.onError(HTTPException(response.code()))
            }
        }
    }
}
