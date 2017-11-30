package me.rei_m.hbfavmaterial.model

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.infra.network.HatenaRssService
import me.rei_m.hbfavmaterial.infra.network.response.BookmarkRssXml
import me.rei_m.hbfavmaterial.model.entity.ArticleEntity
import me.rei_m.hbfavmaterial.model.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.model.util.RssXmlUtil
import org.jsoup.Jsoup

class FavoriteBookmarkModel(private val hatenaRssService: HatenaRssService) {

    companion object {
        private const val BOOKMARK_COUNT_PER_PAGE = 25
    }

    private val userIdSubject = BehaviorSubject.create<String>()
    private val isLoadingSubject = BehaviorSubject.create<Boolean>()
    private val isRefreshingSubject = BehaviorSubject.create<Boolean>()
    private val bookmarkListSubject = BehaviorSubject.create<List<BookmarkEntity>>()
    private val hasNextPageSubject = BehaviorSubject.create<Boolean>()
    private val isRaisedErrorSubject = BehaviorSubject.create<Boolean>()

    private val isRaisedGetNextPageErrorSubject = PublishSubject.create<Unit>()
    private val isRaisedRefreshErrorSubject = PublishSubject.create<Unit>()

    val userId: Observable<String> = userIdSubject
    val isLoading: Observable<Boolean> = isLoadingSubject
    val isRefreshing: Observable<Boolean> = isRefreshingSubject
    val bookmarkList: Observable<List<BookmarkEntity>> = bookmarkListSubject
    val hasNextPage: Observable<Boolean> = hasNextPageSubject
    val isRaisedError: Observable<Boolean> = isRaisedErrorSubject

    val isRaisedGetNextPageError: Observable<Unit> = isRaisedGetNextPageErrorSubject
    val isRaisedRefreshError: Observable<Unit> = isRaisedRefreshErrorSubject

    private var isLoadingNextPage = false

    init {
        isLoadingSubject.onNext(false)
        isRefreshingSubject.onNext(false)
    }

    fun getList(userId: String) {

        if (isLoadingSubject.value) {
            return
        }

        if (userIdSubject.hasValue()) {
            if (userIdSubject.value == userId) {
                bookmarkListSubject.retry()
                hasNextPageSubject.retry()
                return
            } else {
                bookmarkListSubject.onNext(listOf())
            }
        }

        isLoadingSubject.onNext(true)

        userIdSubject.onNext(userId)

        hatenaRssService.favorite(userId, 0).map {
            parseResponse(it)
        }.subscribeAsync({
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

        hatenaRssService.favorite(userId, nextIndex).map {
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

        hatenaRssService.favorite(userId, 0).map {
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
