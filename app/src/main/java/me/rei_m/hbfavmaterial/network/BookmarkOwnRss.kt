package me.rei_m.hbfavmaterial.network

import com.squareup.okhttp.HttpUrl
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import rx.Observable

/**
 * ユーザーのブックマークRSSを取得するクラス.
 */
public class BookmarkOwnRss : AbsBookmarkRss() {

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
