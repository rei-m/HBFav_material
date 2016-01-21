package me.rei_m.hbfavmaterial.network

import me.rei_m.hbfavmaterial.exeptions.HTTPException
import okhttp3.CacheControl
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import rx.Observable
import java.net.HttpURLConnection

/**
 * お気に入りのRSSを取得するクラス.
 */
class BookmarkFavoriteRss() {

    fun request(userId: String, startIndex: Int = 0): Observable<String> {

        return Observable.create { t ->

            val url = HttpUrl.Builder()
                    .scheme("http")
                    .host("b.hatena.ne.jp")
                    .addPathSegment(userId)
                    .addPathSegment("favorite.rss")
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
