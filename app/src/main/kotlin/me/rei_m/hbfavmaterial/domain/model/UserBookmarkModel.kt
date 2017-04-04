package me.rei_m.hbfavmaterial.domain.model

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.constant.ReadAfterFilter
import me.rei_m.hbfavmaterial.domain.entity.ArticleEntity
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.domain.util.RssXmlUtil
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.infra.network.HatenaRssService
import me.rei_m.hbfavmaterial.infra.network.response.BookmarkRssXml
import org.jsoup.Jsoup

class UserBookmarkModel(private val hatenaRssService: HatenaRssService) {

    companion object {
        private const val BOOKMARK_COUNT_PER_PAGE = 20

        private const val TAG_READ_AFTER = "あとで読む"
    }

    private val bookmarkListSubject = PublishSubject.create<List<BookmarkEntity>>()

    val bookmarkList: Observable<List<BookmarkEntity>> = bookmarkListSubject

    private val hasNextPageSubject = PublishSubject.create<Boolean>()

    val hasNextPage: Observable<Boolean> = hasNextPageSubject

    private var readAfterFilterSubject = PublishSubject.create<ReadAfterFilter>()

    val readAfterFilter: Observable<ReadAfterFilter> = readAfterFilterSubject
    
    private val errorSubject = PublishSubject.create<Unit>()

    val error: Observable<Unit> = errorSubject

    private val bookmarkListHolder: MutableList<BookmarkEntity> = mutableListOf()

    private var hasNextPageHolder: Boolean = true

    private var readAfterFilterHolder: ReadAfterFilter = ReadAfterFilter.ALL

    private var isLoading: Boolean = false

    var userId: String = ""

    fun getList(readAfterFilter: ReadAfterFilter) {

        require(userId.isNotEmpty(), {
            "Set userId before call"
        })

        if (isLoading) {
            return
        }

        isLoading = true

        val rss = if (readAfterFilter == ReadAfterFilter.AFTER_READ) {
            hatenaRssService.user(userId, 0, TAG_READ_AFTER)
        } else {
            hatenaRssService.user(userId, 0)
        }

        rss.map {
            parseResponse(it)
        }.subscribeAsync({
            bookmarkListHolder.clear()
            if (it.isNotEmpty()) {
                bookmarkListHolder.addAll(it)
                hasNextPageHolder = true
            } else {
                hasNextPageHolder = false
            }
            bookmarkListSubject.onNext(bookmarkListHolder)
            hasNextPageSubject.onNext(hasNextPageHolder)
            readAfterFilterSubject.onNext(readAfterFilter)
            readAfterFilterHolder = readAfterFilter
        }, {
            errorSubject.onNext(Unit)
        }, {
            isLoading = false
        })
    }

    fun getNextPage() {

        require(userId.isNotEmpty(), {
            "Set userId before call"
        })

        if (isLoading || !hasNextPageHolder) {
            return
        }

        isLoading = true

        val pageCnt = (bookmarkListHolder.size / BOOKMARK_COUNT_PER_PAGE)
        val mod = (bookmarkListHolder.size % BOOKMARK_COUNT_PER_PAGE)

        val nextIndex = if (mod == 0) {
            pageCnt * BOOKMARK_COUNT_PER_PAGE + 1
        } else {
            (pageCnt + 1) * BOOKMARK_COUNT_PER_PAGE + 1
        }

        val rss = if (readAfterFilterHolder == ReadAfterFilter.AFTER_READ) {
            hatenaRssService.user(userId, nextIndex, TAG_READ_AFTER)
        } else {
            hatenaRssService.user(userId, nextIndex)
        }

        rss.map {
            parseResponse(it)
        }.subscribeAsync({
            if (it.isNotEmpty()) {
                bookmarkListHolder.addAll(it)
                bookmarkListSubject.onNext(bookmarkListHolder)
                hasNextPageHolder = true
            } else {
                hasNextPageHolder = false
            }
            hasNextPageSubject.onNext(hasNextPageHolder)
        }, {
            errorSubject.onNext(Unit)
        }, {
            isLoading = false
        })
    }

    private fun parseResponse(response: BookmarkRssXml): List<BookmarkEntity> {
        return response.list.map {
            val parsedContent = Jsoup.parse(it.content)
            val articleEntity = ArticleEntity(
                    title = it.title,
                    url = it.link,
                    bookmarkCount = it.bookmarkCount,
                    iconUrl = RssXmlUtil.extractArticleIcon(parsedContent),
                    body = RssXmlUtil.extractArticleBodyForBookmark(parsedContent),
                    bodyImageUrl = RssXmlUtil.extractArticleImageUrl(parsedContent))
            BookmarkEntity(
                    article = articleEntity,
                    description = it.description,
                    creator = it.creator,
                    date = RssXmlUtil.parseStringToDate(it.dateString),
                    bookmarkIconUrl = RssXmlUtil.extractProfileIcon(parsedContent))
        }
    }
}
