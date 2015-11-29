package me.rei_m.hbfavkotlin.network

import com.squareup.okhttp.HttpUrl
import me.rei_m.hbfavkotlin.entities.BookmarkEntity
import rx.Observable

public final class BookmarkOwnRss : AbsBookmarkRss() {

    public fun request(userId: String, startIndex: Int = 0): Observable<BookmarkEntity> {
        val url = HttpUrl.Builder()
                .scheme("http")
                .host("b.hatena.ne.jp")
                .addPathSegment(userId)
                .addPathSegment("rss")
                .addQueryParameter("of", startIndex.toString())
                .build()

        return super.request(url)

    }
}