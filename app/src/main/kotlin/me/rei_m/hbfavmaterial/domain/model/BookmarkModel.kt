package me.rei_m.hbfavmaterial.domain.model

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.constant.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.domain.entity.BookmarkUserEntity
import me.rei_m.hbfavmaterial.domain.util.ApiUtil
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.infra.network.HatenaApiService
import me.rei_m.hbfavmaterial.presentation.util.BookmarkUtil

class BookmarkModel(private val hatenaApiService: HatenaApiService) {

    var articleUrl: String = ""

    private val userListSubject = PublishSubject.create<List<BookmarkUserEntity>>()

    val userList: Observable<List<BookmarkUserEntity>> = userListSubject

    private var bookmarkCommentFilterSubject = PublishSubject.create<BookmarkCommentFilter>()

    val bookmarkCommentFilter: Observable<BookmarkCommentFilter> = bookmarkCommentFilterSubject

    private val errorSubject = PublishSubject.create<Unit>()

    val error: Observable<Unit> = errorSubject

    private var isLoading: Boolean = false

    fun getUserList(bookmarkCommentFilter: BookmarkCommentFilter) {

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
            userListSubject.onNext(it)
            bookmarkCommentFilterSubject.onNext(bookmarkCommentFilter)
        }, {
            errorSubject.onNext(Unit)
        }, {
            isLoading = false
        })
    }
}
