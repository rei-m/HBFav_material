package me.rei_m.hbfavmaterial.model

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.constant.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.infra.network.HatenaApiService
import me.rei_m.hbfavmaterial.model.entity.BookmarkUserEntity
import me.rei_m.hbfavmaterial.model.util.ApiUtil
import me.rei_m.hbfavmaterial.presentation.util.BookmarkUtil

class BookmarkModel(private val hatenaApiService: HatenaApiService) {

    private var userList: List<BookmarkUserEntity> = listOf()
        private set(value) {
            field = value
            userListUpdatedEventSubject.onNext(value)
        }

    private var bookmarkCommentFilter: BookmarkCommentFilter = BookmarkCommentFilter.ALL
        private set(value) {
            field = value
            bookmarkCommentFilterUpdatedEventSubject.onNext(value)
        }

    private val userListUpdatedEventSubject = PublishSubject.create<List<BookmarkUserEntity>>()
    private val bookmarkCommentFilterUpdatedEventSubject = PublishSubject.create<BookmarkCommentFilter>()
    private val errorSubject = PublishSubject.create<Unit>()

    val userListUpdatedEvent: Observable<List<BookmarkUserEntity>> = userListUpdatedEventSubject
    val bookmarkCommentFilterUpdatedEvent: Observable<BookmarkCommentFilter> = bookmarkCommentFilterUpdatedEventSubject
    val error: Observable<Unit> = errorSubject

    private var isLoading: Boolean = false

    fun getUserList(articleUrl: String, bookmarkCommentFilter: BookmarkCommentFilter) {

        require(articleUrl.isNotEmpty()) {
            "Set articleUrl before call"
        }

        if (isLoading) {
            return
        }

        isLoading = true

        hatenaApiService.entry(articleUrl).map { (_, bookmarks) ->
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
        }.subscribeAsync({
            userList = it
            if (this.bookmarkCommentFilter != bookmarkCommentFilter) {
                this.bookmarkCommentFilter = bookmarkCommentFilter
            }
        }, {
            errorSubject.onNext(Unit)
        }, {
            isLoading = false
        })
    }
}
