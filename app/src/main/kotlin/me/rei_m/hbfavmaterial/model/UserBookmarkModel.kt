/*
 * Copyright (c) 2017. Rei Matsushita
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package me.rei_m.hbfavmaterial.model

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.constant.ReadAfterFilter
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.infra.network.HatenaRssService
import me.rei_m.hbfavmaterial.infra.network.response.BookmarkRssXml
import me.rei_m.hbfavmaterial.model.entity.Article
import me.rei_m.hbfavmaterial.model.entity.Bookmark
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
    private val bookmarkListSubject = BehaviorSubject.create<List<Bookmark>>()
    private val readAfterFilterSubject = BehaviorSubject.create<ReadAfterFilter>()
    private val hasNextPageSubject = BehaviorSubject.create<Boolean>()
    private val isRaisedErrorSubject = BehaviorSubject.create<Boolean>()

    private val isRaisedGetNextPageErrorSubject = PublishSubject.create<Unit>()
    private val isRaisedRefreshErrorSubject = PublishSubject.create<Unit>()

    val userId: Observable<String> = userIdSubject
    val isLoading: Observable<Boolean> = isLoadingSubject
    val isRefreshing: Observable<Boolean> = isRefreshingSubject
    val bookmarkList: Observable<List<Bookmark>> = bookmarkListSubject
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

        if (!bookmarkListSubject.hasValue() || isLoadingNextPage || !hasNextPageSubject.value) {
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

    private fun parseResponse(response: BookmarkRssXml): List<Bookmark> {
        return response.list.map {
            val parsedContent = Jsoup.parse(it.content)
            val article = Article(
                    title = it.title,
                    url = it.link,
                    bookmarkCount = it.bookmarkCount,
                    iconUrl = RssXmlUtil.extractArticleIcon(parsedContent),
                    body = RssXmlUtil.extractArticleBodyForBookmark(parsedContent),
                    bodyImageUrl = RssXmlUtil.extractArticleImageUrl(parsedContent))
            Bookmark(
                    article = article,
                    description = it.description,
                    creator = it.creator,
                    date = RssXmlUtil.parseStringToDate(it.dateString),
                    bookmarkIconUrl = RssXmlUtil.extractProfileIcon(parsedContent))
        }
    }
}
