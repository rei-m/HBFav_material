package me.rei_m.hbfavmaterial.model

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
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

    private val userIdSubject = BehaviorSubject.create<String>()
    private val isLoadingSubject = BehaviorSubject.create<Boolean>()
    private val isRefreshingSubject = BehaviorSubject.create<Boolean>()
    private val bookmarkListSubject = BehaviorSubject.create<List<BookmarkEntity>>()
    private val readAfterFilterSubject = BehaviorSubject.create<ReadAfterFilter>()
    private val hasNextPageSubject = BehaviorSubject.create<Boolean>()
    private val isRaisedErrorSubject = BehaviorSubject.create<Boolean>()

    private val isRaisedGetNextPageErrorSubject = PublishSubject.create<Unit>()
    private val isRaisedRefreshErrorSubject = PublishSubject.create<Unit>()

    val userId: Observable<String> = userIdSubject
    val isLoading: Observable<Boolean> = isLoadingSubject
    val isRefreshing: Observable<Boolean> = isRefreshingSubject
    val bookmarkList: Observable<List<BookmarkEntity>> = bookmarkListSubject
    val readAfterFilter: Observable<ReadAfterFilter> = readAfterFilterSubject
    val hasNextPage: Observable<Boolean> = hasNextPageSubject
    val isRaisedError: Observable<Boolean> = isRaisedErrorSubject

    val isRaisedGetNextPageError: Observable<Unit> = isRaisedGetNextPageErrorSubject
    val isRaisedRefreshError: Observable<Unit> = isRaisedRefreshErrorSubject

    private var isLoadingNextPage = false

    init {
        isLoadingSubject.onNext(false)
        isRefreshingSubject.onNext(false)
        readAfterFilterSubject.onNext(ReadAfterFilter.ALL)
    }

    fun getList(userId: String, readAfterFilter: ReadAfterFilter) {

        if (isLoadingSubject.value) {
            return
        }

        if (userIdSubject.hasValue()) {
            if (userIdSubject.value == userId && readAfterFilterSubject.value == readAfterFilter) {
                bookmarkListSubject.retry()
                hasNextPageSubject.retry()
                return
            } else {
                bookmarkListSubject.onNext(listOf())
            }
        }

        isLoadingSubject.onNext(true)

        userIdSubject.onNext(userId)

        val rss = if (readAfterFilter == ReadAfterFilter.AFTER_READ) {
            hatenaRssService.user(userId, 0, TAG_READ_AFTER)
        } else {
            hatenaRssService.user(userId, 0)
        }

        rss.map {
            parseResponse(it)
        }.subscribeAsync({
            if (readAfterFilterSubject.value != readAfterFilter) {
                readAfterFilterSubject.onNext(readAfterFilter)
                bookmarkListSubject.onNext(listOf())
            }
            bookmarkListSubject.onNext(it)
            hasNextPageSubject.onNext(it.isNotEmpty())
            isRaisedErrorSubject.onNext(false)
        }, {
            isRaisedErrorSubject.onNext(true)
        }, {
            isLoadingSubject.onNext(false)
        })
    }

    fun getNextPage(userId: String) {

        if (isLoadingNextPage || !hasNextPageSubject.value) {
            return
        }

        isLoadingNextPage = true

        val pageCnt = (bookmarkListSubject.value.size / BOOKMARK_COUNT_PER_PAGE)
        val mod = (bookmarkListSubject.value.size % BOOKMARK_COUNT_PER_PAGE)

        val nextIndex = if (mod == 0) {
            pageCnt * BOOKMARK_COUNT_PER_PAGE + 1
        } else {
            (pageCnt + 1) * BOOKMARK_COUNT_PER_PAGE + 1
        }

        val rss = if (readAfterFilterSubject.value == ReadAfterFilter.AFTER_READ) {
            hatenaRssService.user(userId, nextIndex, TAG_READ_AFTER)
        } else {
            hatenaRssService.user(userId, nextIndex)
        }

        rss.map {
            parseResponse(it)
        }.subscribeAsync({
            if (it.isNotEmpty()) {
                bookmarkListSubject.onNext(bookmarkListSubject.value + it)
                hasNextPageSubject.onNext(true)
            } else {
                hasNextPageSubject.onNext(false)
            }
        }, {
            isRaisedGetNextPageErrorSubject.onNext(Unit)
        }, {
            isLoadingNextPage = false
        })
    }

    fun refreshList(userId: String) {

        if (isRefreshingSubject.value) {
            return
        }

        isRefreshingSubject.onNext(true)

        val rss = if (readAfterFilterSubject.value == ReadAfterFilter.AFTER_READ) {
            hatenaRssService.user(userId, 0, TAG_READ_AFTER)
        } else {
            hatenaRssService.user(userId, 0)
        }

        rss.map {
            parseResponse(it)
        }.subscribeAsync({
            bookmarkListSubject.onNext(listOf())
            bookmarkListSubject.onNext(it)
            hasNextPageSubject.onNext(it.isNotEmpty())
            isRaisedErrorSubject.onNext(false)
        }, {
            isRaisedRefreshErrorSubject.onNext(Unit)
        }, {
            isRefreshingSubject.onNext(false)
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
