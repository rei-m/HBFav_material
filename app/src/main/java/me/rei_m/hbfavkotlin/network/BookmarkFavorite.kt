package me.rei_m.hbfavkotlin.network

import com.squareup.okhttp.CacheControl
import com.squareup.okhttp.HttpUrl
import java.net.HttpURLConnection

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request

import rx.Observable
import rx.Subscriber

import kotlin.dom.parseXml

import me.rei_m.hbfavkotlin.models.Bookmark

public final class BookmarkFavorite private constructor() {

    companion object {

        public var isLoading = false
            private set

        public fun request(startIndex: Int): Observable<Bookmark> {

            isLoading = true

            return Observable.create(object : Observable.OnSubscribe<Bookmark> {

                override fun call(t: Subscriber<in Bookmark>) {

                    val url = HttpUrl.Builder()
                            .scheme("http")
                            .host("b.hatena.ne.jp")
                            .addPathSegment("Rei19")
                            .addPathSegment("favorite.rss")
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

                        for(i in 0..feedCount - 1){
                            val feed = feeds.item(i)

                            var title = ""
                            var link = ""
                            var description = ""
                            var creator = ""
                            var date = ""
                            var bookmarkCount = 0

                            for(j in 0..feed.childNodes.length - 1) {
                                val feedItem = feed.childNodes.item(j)
                                when (feedItem.nodeName) {
                                    "title" ->
                                        title = feedItem.textContent
                                    "link" ->
                                        link = feedItem.textContent
                                    "description" ->
                                        description = feedItem.textContent
                                    "dc:creator" ->
                                        creator = feedItem.textContent
                                    "dc:date" ->
                                        date = feedItem.textContent
                                    "hatena:bookmarkcount" ->
                                        bookmarkCount = feedItem.textContent.toInt()
                                }
                             }

                            val bookmark = Bookmark(
                                    title,
                                    link,
                                    description,
                                    creator,
                                    date,
                                    bookmarkCount)

                            t.onNext(bookmark)
                        }
                    } else {
                        t.onError(Throwable(response.code().toString()))
                    }

                    t.onCompleted()

                    isLoading = false
                }
            })
        }
    }
}