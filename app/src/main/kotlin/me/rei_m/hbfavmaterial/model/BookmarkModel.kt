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
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.constant.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.infra.network.HatenaApiService
import me.rei_m.hbfavmaterial.model.entity.BookmarkUser
import me.rei_m.hbfavmaterial.model.util.ApiUtil
import me.rei_m.hbfavmaterial.presentation.util.BookmarkUtil

class BookmarkModel(private val hatenaApiService: HatenaApiService) {

    private val isLoadingSubject = BehaviorSubject.create<Boolean>()
    private val isRefreshingSubject = BehaviorSubject.create<Boolean>()
    private val bookmarkUserListSubject = BehaviorSubject.create<List<BookmarkUser>>()
    private val bookmarkCommentFilterSubject = BehaviorSubject.create<BookmarkCommentFilter>()
    private val isRaisedGetErrorSubject = BehaviorSubject.create<Boolean>()

    private val isRaisedRefreshErrorSubject = PublishSubject.create<Unit>()

    val isLoading: Observable<Boolean> = isLoadingSubject
    val isRefreshing: Observable<Boolean> = isRefreshingSubject
    val bookmarkUserList: Observable<List<BookmarkUser>> = bookmarkUserListSubject
    val isRaisedGetError: Observable<Boolean> = isRaisedGetErrorSubject

    val isRaisedRefreshError: Observable<Unit> = isRaisedRefreshErrorSubject

    init {
        isLoadingSubject.onNext(false)
        isRefreshingSubject.onNext(false)
    }

    fun getUserList(articleUrl: String, bookmarkCommentFilter: BookmarkCommentFilter) {

        require(articleUrl.isNotEmpty()) {
            "Url is Empty"
        }

        if (isLoadingSubject.value) {
            return
        }

        isLoadingSubject.onNext(true)

        fetch(articleUrl, bookmarkCommentFilter).subscribeAsync({
            bookmarkUserListSubject.onNext(it)
            bookmarkCommentFilterSubject.onNext(bookmarkCommentFilter)
            isRaisedGetErrorSubject.onNext(false)
        }, {
            isRaisedGetErrorSubject.onNext(true)
        }, {
            isLoadingSubject.onNext(false)
        })
    }

    fun refreshUserList(articleUrl: String, bookmarkCommentFilter: BookmarkCommentFilter) {

        require(articleUrl.isNotEmpty()) {
            "Url is Empty"
        }

        if (isRefreshingSubject.value) {
            return
        }

        isRefreshingSubject.onNext(true)

        fetch(articleUrl, bookmarkCommentFilter).subscribeAsync({
            bookmarkUserListSubject.onNext(listOf())
            bookmarkUserListSubject.onNext(it)
            isRaisedGetErrorSubject.onNext(false)
        }, {
            isRaisedRefreshErrorSubject.onNext(Unit)
        }, {
            isRefreshingSubject.onNext(false)
        })
    }

    private fun fetch(articleUrl: String, bookmarkCommentFilter: BookmarkCommentFilter): Single<List<BookmarkUser>> {
        return hatenaApiService.entry(articleUrl).map { (_, bookmarks) ->
            val bookmarkList = bookmarks.map { (timestamp, comment, user, _) ->
                BookmarkUser(creator = user,
                        iconUrl = BookmarkUtil.getIconImageUrlFromId(user),
                        comment = comment,
                        createdAt = ApiUtil.parseStringToDate(timestamp))
            }

            return@map if (bookmarkCommentFilter == BookmarkCommentFilter.COMMENT) {
                bookmarkList.filter { it.comment.isNotEmpty() }
            } else {
                bookmarkList
            }
        }
    }
}
