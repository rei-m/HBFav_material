package me.rei_m.hbfavmaterial.model

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.constant.ReadAfterFilter
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.infra.network.HatenaRssService
import me.rei_m.hbfavmaterial.infra.network.response.BookmarkRssXml
import me.rei_m.hbfavmaterial.model.entity.ArticleEntity
import me.rei_m.hbfavmaterial.model.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.model.util.RssXmlUtil
import org.jsoup.Jsoup

class UserBookmarkModel(private val hatenaRssService: HatenaRssService) {

    companion object {
        private const val BOOKMARK_COUNT_PER_PAGE = 20

        private const val TAG_READ_AFTER = "あとで読む"
    }

    var bookmarkList: List<BookmarkEntity> = listOf()
        private set(value) {
            field = value
            bookmarkListUpdatedEventSubject.onNext(value)
        }

    var hasNextPage: Boolean = false
        private set(value) {
            field = value
            hasNextPageUpdatedEventSubject.onNext(value)
        }

    var readAfterFilter: ReadAfterFilter = ReadAfterFilter.ALL
        private set(value) {
            field = value
            readAfterFilterUpdatedEventSubject.onNext(value)
        }

    private val bookmarkListUpdatedEventSubject = PublishSubject.create<List<BookmarkEntity>>()
    private val hasNextPageUpdatedEventSubject = PublishSubject.create<Boolean>()
    private val readAfterFilterUpdatedEventSubject = PublishSubject.create<ReadAfterFilter>()
    private val errorSubject = PublishSubject.create<Unit>()

    val bookmarkListUpdatedEvent: Observable<List<BookmarkEntity>> = bookmarkListUpdatedEventSubject
    val hasNextPageUpdatedEvent: Observable<Boolean> = hasNextPageUpdatedEventSubject
    val readAfterFilterUpdatedEvent: Observable<ReadAfterFilter> = readAfterFilterUpdatedEventSubject
    val error: Observable<Unit> = errorSubject

    private var isLoading: Boolean = false

    private var userId: String = ""

    fun getList(userId: String, readAfterFilter: ReadAfterFilter) {

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
            this.userId = userId
            bookmarkList = it
            hasNextPage = it.isNotEmpty()
            if (this.readAfterFilter != readAfterFilter) {
                this.readAfterFilter = readAfterFilter
            }
        }, {
            errorSubject.onNext(Unit)
        }, {
            isLoading = false
        })
    }

    fun getNextPage() {

        require(userId.isNotEmpty(), {
            "Call getList before call getNextPage"
        })

        if (isLoading || !hasNextPage) {
            return
        }

        isLoading = true

        val pageCnt = (bookmarkList.size / BOOKMARK_COUNT_PER_PAGE)
        val mod = (bookmarkList.size % BOOKMARK_COUNT_PER_PAGE)

        val nextIndex = if (mod == 0) {
            pageCnt * BOOKMARK_COUNT_PER_PAGE + 1
        } else {
            (pageCnt + 1) * BOOKMARK_COUNT_PER_PAGE + 1
        }

        val rss = if (readAfterFilter == ReadAfterFilter.AFTER_READ) {
            hatenaRssService.user(userId, nextIndex, TAG_READ_AFTER)
        } else {
            hatenaRssService.user(userId, nextIndex)
        }

        rss.map {
            parseResponse(it)
        }.subscribeAsync({
            if (it.isNotEmpty()) {
                val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
                bookmarkList.addAll(this.bookmarkList)
                bookmarkList.addAll(it)
                this.bookmarkList = bookmarkList
                hasNextPage = true
            } else {
                hasNextPage = false
            }
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
