package me.rei_m.hbfavmaterial.network

import com.squareup.okhttp.CacheControl
import com.squareup.okhttp.HttpUrl
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import me.rei_m.hbfavmaterial.exeptions.HTTPException
import rx.Observable
import java.net.HttpURLConnection

/**
 * ユーザーのブックマークRSSを取得するクラス.
 */
class BookmarkOwnRss {

    fun request(userId: String, startIndex: Int = 0): Observable<String> {

        return Observable.create { t ->

            val url = HttpUrl.Builder()
                    .scheme("http")
                    .host("b.hatena.ne.jp")
                    .addPathSegment(userId)
                    .addPathSegment("rss")
                    .addQueryParameter("of", startIndex.toString())
                    .build()

            val request = Request.Builder()
                    .url(url)
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build()

            val response = OkHttpClient().newCall(request).execute()
            if (response.code() == HttpURLConnection.HTTP_OK) {
                t.onNext(response.body().string())
            } else {
                t.onError(HTTPException(response.code()))
            }

            t.onCompleted()
        }
    }
}
