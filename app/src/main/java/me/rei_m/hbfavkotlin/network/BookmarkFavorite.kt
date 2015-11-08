package me.rei_m.hbfavkotlin.network

import java.net.HttpURLConnection

import com.squareup.okhttp.CacheControl
import com.squareup.okhttp.HttpUrl
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request

import rx.Observable
import rx.Subscriber

import kotlin.dom.parseXml

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.w3c.dom.Node

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

                        for(i_feed in 0..feedCount - 1){
                            t.onNext(createBookmarkFromFeed(feeds.item(i_feed)))
                        }

                    } else {
                        t.onError(Throwable(response.code().toString()))
                    }

                    t.onCompleted()

                    isLoading = false
                }
            })
        }

        private fun createBookmarkFromFeed(feed: Node): Bookmark {

            var title = ""
            var link = ""
            var description = ""
            var creator = ""
            var date = ""
            var bookmarkCount = 0
            var content = ""

            for(i_node in 0..feed.childNodes.length - 1) {
                val feedItem = feed.childNodes.item(i_node)
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
                    "content:encoded" ->
                        content = feedItem.textContent
                }
            }

            val parsedContent = Jsoup.parse(content)

            return Bookmark(
                    title,
                    link,
                    description,
                    creator,
                    date,
                    bookmarkCount,
                    extractProfileIcon(parsedContent),
                    extractArticleIcon(parsedContent),
                    extractArticleBody(parsedContent),
                    extractArticleImageUrl(parsedContent))
        }

        private fun extractProfileIcon(content: Document): String = content
                .getElementsByClass("profile-image")
                .first()
                .attr("src")
                .replace("profile_s", "profile", true)

        private fun extractArticleIcon(content: Document): String = content
                .getElementsByTag("cite")
                .first()
                .getElementsByTag("img")
                .first()
                .attr("src")

        private fun extractArticleBody(content: Document): String = content
                .getElementsByTag("p")
                .eq(1)
                .text()

        private fun extractArticleImageUrl(content: Document): String {
            val articleImageElement = content
                    .getElementsByClass("entry-image")
                    .first()

            return if(articleImageElement == null) "" else articleImageElement.attr("src")
        }
    }
}