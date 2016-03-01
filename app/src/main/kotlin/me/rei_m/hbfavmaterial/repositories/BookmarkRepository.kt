package me.rei_m.hbfavmaterial.repositories

import kotlinx.dom.parseXml
import me.rei_m.hbfavmaterial.entities.ArticleEntity
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.enums.ReadAfterFilter
import me.rei_m.hbfavmaterial.network.BookmarkFavoriteRss
import me.rei_m.hbfavmaterial.network.BookmarkOwnRss
import me.rei_m.hbfavmaterial.network.EntryApi
import me.rei_m.hbfavmaterial.utils.ApiUtil
import me.rei_m.hbfavmaterial.utils.RssXmlUtil
import rx.Observable
import java.util.*

/**
 * ブックマーク情報のリポジトリ.
 */
open class BookmarkRepository() {

    /**
     * お気に入りのユーザーのブックマーク情報を取得する.
     */
    open fun findByUserIdForFavorite(userId: String, startIndex: Int = 0): Observable<List<BookmarkEntity>> {
        return BookmarkFavoriteRss()
                .request(userId, startIndex)
                .map { response -> parseRssResponse(response) }
    }

    /**
     * ユーザーのブックマーク情報を取得する.
     */
    open fun findByUserId(userId: String, readAfterFilter: ReadAfterFilter, startIndex: Int = 0): Observable<List<BookmarkEntity>> {
        return BookmarkOwnRss()
                .request(userId, readAfterFilter, startIndex)
                .map { response -> parseRssResponse(response) }
    }

    /**
     * URLをキーにブックマーク情報を取得する.
     */
    open fun findByArticleUrl(articleUrl: String): Observable<List<BookmarkEntity>> {
        return EntryApi()
                .request(articleUrl)
                .map { response ->
                    return@map ArrayList<BookmarkEntity>().apply {
                        response.bookmarks.forEach { v ->
                            val articleEntity = ArticleEntity(
                                    title = "",
                                    url = articleUrl,
                                    bookmarkCount = response.count,
                                    iconUrl = "",
                                    body = "",
                                    bodyImageUrl = "")

                            add(BookmarkEntity(
                                    articleEntity = articleEntity,
                                    description = v.comment,
                                    creator = v.user,
                                    date = ApiUtil.parseStringToDate(v.timestamp),
                                    bookmarkIconUrl = "",
                                    tags = v.tags))
                        }
                    }
                }
    }

    /**
     * RSSのレスポンスをパースしてブックマーク情報に変換する.
     */
    private fun parseRssResponse(response: String): List<BookmarkEntity> {
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
