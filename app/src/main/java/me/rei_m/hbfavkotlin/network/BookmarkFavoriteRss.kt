package me.rei_m.hbfavkotlin.network

import com.squareup.okhttp.HttpUrl
import me.rei_m.hbfavkotlin.entities.BookmarkEntity
import rx.Observable

public final class BookmarkFavoriteRss : AbsBookmarkRss() {

    public fun request(startIndex: Int = 0): Observable<BookmarkEntity> {
        val url = HttpUrl.Builder()
                .scheme("http")
                .host("b.hatena.ne.jp")
                .addPathSegment("Rei19")
                .addPathSegment("favorite.rss")
                .addQueryParameter("of", startIndex.toString())
                .build()

        return super.request(url)
    }
}