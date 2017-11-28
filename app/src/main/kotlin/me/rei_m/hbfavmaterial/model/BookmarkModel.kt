package me.rei_m.hbfavmaterial.model

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.constant.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.infra.network.HatenaApiService
import me.rei_m.hbfavmaterial.model.entity.BookmarkUserEntity
import me.rei_m.hbfavmaterial.model.util.ApiUtil
import me.rei_m.hbfavmaterial.presentation.util.BookmarkUtil

class BookmarkModel(private val hatenaApiService: HatenaApiService) {

    private val isLoadingSubject = BehaviorSubject.create<Boolean>()
    private val isRefreshingSubject = BehaviorSubject.create<Boolean>()
    private val bookmarkUserListSubject = BehaviorSubject.create<List<BookmarkUserEntity>>()
    private val isRaisedGetErrorSubject = BehaviorSubject.create<Boolean>()

    private val isRaisedRefreshErrorSubject = PublishSubject.create<Unit>()

    val isLoading: Observable<Boolean> = isLoadingSubject
    val isRefreshing: Observable<Boolean> = isRefreshingSubject
    val bookmarkUserList: Observable<List<BookmarkUserEntity>> = bookmarkUserListSubject
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
            bookmarkUserListSubject.onNext(listOf())
            bookmarkUserListSubject.onNext(it)
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

    private fun fetch(articleUrl: String, bookmarkCommentFilter: BookmarkCommentFilter): Single<List<BookmarkUserEntity>> {
        return hatenaApiService.entry(articleUrl).map { (_, bookmarks) ->
            val bookmarkList = bookmarks.map { (timestamp, comment, user, _) ->
                BookmarkUserEntity(creator = user,
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
