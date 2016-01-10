package me.rei_m.hbfavmaterial.repositories

import kotlinx.dom.parseXml
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.network.BookmarkFavoriteRss
import me.rei_m.hbfavmaterial.network.BookmarkOwnRss
import me.rei_m.hbfavmaterial.utils.RssXmlUtil
import rx.Observable
import java.util.*

/**
 * ブックマーク情報のリポジトリ.
 */
class BookmarkRepository() {

    /**
     * お気に入りのユーザーのブックマーク情報を取得する.
     */
    fun fetchFavorite(userId: String, startIndex: Int = 0): Observable<List<BookmarkEntity>> {
        return BookmarkFavoriteRss()
                .request(userId, startIndex)
                .map { response -> parseResponse(response) }
    }

    /**
     * ユーザーのブックマーク情報を取得する.
     */
    fun fetchOwn(userId: String, startIndex: Int = 0): Observable<List<BookmarkEntity>> {
        return BookmarkOwnRss()
                .request(userId, startIndex)
                .map { response -> parseResponse(response) }
    }

    /**
     * レスポンスをパースしてブックマーク情報に変換する.
     */
    private fun parseResponse(response: String): List<BookmarkEntity> {
        return ArrayList<BookmarkEntity>().apply {
            val document = parseXml(response.byteInputStream())
            val feeds = document.getElementsByTagName("item")
            val feedCount = feeds.length
            for (i_feed in 0..feedCount - 1) {
                add(RssXmlUtil.createBookmarkFromFeed(feeds.item(i_feed)))
            }
        }
    }
}
