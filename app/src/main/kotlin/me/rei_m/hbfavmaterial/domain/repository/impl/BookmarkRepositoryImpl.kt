package me.rei_m.hbfavmaterial.domain.repository.impl

import io.reactivex.Single
import me.rei_m.hbfavmaterial.constant.ReadAfterFilter
import me.rei_m.hbfavmaterial.domain.entity.ArticleEntity
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.domain.repository.BookmarkRepository
import me.rei_m.hbfavmaterial.domain.util.ApiUtil
import me.rei_m.hbfavmaterial.domain.util.RssXmlUtil
import me.rei_m.hbfavmaterial.infra.network.HatenaApiService
import me.rei_m.hbfavmaterial.infra.network.HatenaRssService
import org.jsoup.Jsoup

class BookmarkRepositoryImpl(private val hatenaRssService: HatenaRssService,
                             private val hatenaApiService: HatenaApiService) : BookmarkRepository {

    override fun findByUserIdForFavorite(userId: String, startIndex: Int): Single<List<BookmarkEntity>> {
        return hatenaRssService.favorite(userId, startIndex).map { response ->
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

    override fun findByUserId(userId: String, readAfterFilter: ReadAfterFilter, startIndex: Int): Single<List<BookmarkEntity>> {

        val rss = if (readAfterFilter == ReadAfterFilter.AFTER_READ) {
            hatenaRssService.user(userId, startIndex, "あとで読む")
        } else {
            hatenaRssService.user(userId, startIndex)
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

    override fun findByArticleUrl(articleUrl: String): Single<List<BookmarkEntity>> {
        return hatenaApiService.entry(articleUrl).map { (count, bookmarks) ->
            return@map arrayListOf<BookmarkEntity>().apply {
                bookmarks.forEach { (timestamp, comment, user, tags) ->
                    val articleEntity = ArticleEntity(
                            title = "",
                            url = articleUrl,
                            bookmarkCount = count,
                            iconUrl = "",
                            body = "",
                            bodyImageUrl = "")

                    add(BookmarkEntity(
                            articleEntity = articleEntity,
                            description = comment,
                            creator = user,
                            date = ApiUtil.parseStringToDate(timestamp),
                            bookmarkIconUrl = "",
                            tags = tags))
                }
            }
        }
    }
}
