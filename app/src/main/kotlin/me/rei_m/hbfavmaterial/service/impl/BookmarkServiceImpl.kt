package me.rei_m.hbfavmaterial.service.impl

import me.rei_m.hbfavmaterial.entities.ArticleEntity
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.enums.ReadAfterFilter
import me.rei_m.hbfavmaterial.network.HatenaApiService
import me.rei_m.hbfavmaterial.network.HatenaRssService
import me.rei_m.hbfavmaterial.network.RetrofitManager
import me.rei_m.hbfavmaterial.service.BookmarkService
import me.rei_m.hbfavmaterial.utils.ApiUtil
import me.rei_m.hbfavmaterial.utils.RssXmlUtil
import org.jsoup.Jsoup
import rx.Observable
import java.util.*

class BookmarkServiceImpl : BookmarkService {

    override fun findByUserIdForFavorite(userId: String, startIndex: Int): Observable<List<BookmarkEntity>> {
        return RetrofitManager.xml
                .create(HatenaRssService::class.java)
                .favorite(userId, startIndex)
                .map { response ->
                    response.list.map {
                        val parsedContent = Jsoup.parse(it.content)
                        val articleEntity = ArticleEntity(
                                title = it.title,
                                url = it.link,
                                bookmarkCount = it.bookmarkCount,
                                iconUrl = RssXmlUtil.extractArticleIcon(parsedContent),
                                body = RssXmlUtil.extractArticleBodyForBookmark(parsedContent),
                                bodyImageUrl = RssXmlUtil.extractArticleImageUrl(parsedContent))
                        BookmarkEntity(
                                articleEntity = articleEntity,
                                description = it.description,
                                creator = it.creator,
                                date = RssXmlUtil.parseStringToDate(it.dateString),
                                bookmarkIconUrl = RssXmlUtil.extractProfileIcon(parsedContent))
                    }
                }
    }

    override fun findByUserId(userId: String, readAfterFilter: ReadAfterFilter, startIndex: Int): Observable<List<BookmarkEntity>> {

        val rss = if (readAfterFilter == ReadAfterFilter.AFTER_READ) {
            RetrofitManager.xml.create(HatenaRssService::class.java).user(userId, startIndex, "あとで読む")
        } else {
            RetrofitManager.xml.create(HatenaRssService::class.java).user(userId, startIndex)
        }

        return rss.map { response ->
            response.list.map {
                val parsedContent = Jsoup.parse(it.content)
                val articleEntity = ArticleEntity(
                        title = it.title,
                        url = it.link,
                        bookmarkCount = it.bookmarkCount,
                        iconUrl = RssXmlUtil.extractArticleIcon(parsedContent),
                        body = RssXmlUtil.extractArticleBodyForBookmark(parsedContent),
                        bodyImageUrl = RssXmlUtil.extractArticleImageUrl(parsedContent))
                BookmarkEntity(
                        articleEntity = articleEntity,
                        description = it.description,
                        creator = it.creator,
                        date = RssXmlUtil.parseStringToDate(it.dateString),
                        bookmarkIconUrl = RssXmlUtil.extractProfileIcon(parsedContent))
            }
        }
    }

    override fun findByArticleUrl(articleUrl: String): Observable<List<BookmarkEntity>> {
        return RetrofitManager.json.create(HatenaApiService::class.java)
                .entry(articleUrl)
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
}