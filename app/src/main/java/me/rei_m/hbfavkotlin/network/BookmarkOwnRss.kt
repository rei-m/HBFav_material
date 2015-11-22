package me.rei_m.hbfavkotlin.network

import com.squareup.okhttp.CacheControl
import com.squareup.okhttp.HttpUrl
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import kotlinx.dom.parseXml
import me.rei_m.hbfavkotlin.entities.BookmarkEntity
import me.rei_m.hbfavkotlin.utils.BookmarkXmlUtils
import rx.Observable
import java.net.HttpURLConnection

public final class BookmarkOwnRss private constructor() {

    companion object {

        public fun request(startIndex: Int = 0): Observable<BookmarkEntity> {

            return Observable.create({ t ->

                val url = HttpUrl.Builder()
                        .scheme("http")
                        .host("b.hatena.ne.jp")
                        .addPathSegment("Rei19")
                        .addPathSegment("rss")
                        .addQueryParameter("of", startIndex.toString())
                        .build()

                val request = Request.Builder()
                        .url(url)
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .build()

                val response = OkHttpClient().newCall(request).execute()

                if (response.code() == HttpURLConnection.HTTP_OK) {

                    val document = parseXml(response.body().byteStream())

                    val feeds = document.getElementsByTagName("item")

                    val feedCount = feeds.length

                    for (i_feed in 0..feedCount - 1) {
                        t.onNext(BookmarkXmlUtils.createBookmarkFromFeed(feeds.item(i_feed)))
                    }

                } else {
                    t.onError(Throwable(response.code().toString()))
                }

                t.onCompleted()
            })
        }
    }
}