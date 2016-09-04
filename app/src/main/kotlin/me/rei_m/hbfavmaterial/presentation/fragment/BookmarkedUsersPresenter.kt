package me.rei_m.hbfavmaterial.presentation.fragment

import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.enum.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.usecase.GetBookmarkedUsersUsecase
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

class BookmarkedUsersPresenter(private val getBookmarkedUsersUsecase: GetBookmarkedUsersUsecase,
                               private var bookmarkList: List<BookmarkEntity> = mutableListOf()) : BookmarkedUsersContact.Actions {

    private lateinit var view: BookmarkedUsersContact.View

    private lateinit var bookmarkEntity: BookmarkEntity

    private var subscription: CompositeSubscription? = null

    private var isLoading = false

    override var bookmarkCommentFilter: BookmarkCommentFilter = BookmarkCommentFilter.ALL

    override fun onCreate(view: BookmarkedUsersContact.View,
                          bookmarkEntity: BookmarkEntity,
                          bookmarkCommentFilter: BookmarkCommentFilter) {
        this.view = view
        this.bookmarkEntity = bookmarkEntity
        this.bookmarkCommentFilter = bookmarkCommentFilter
    }

    override fun onResume() {
        subscription = CompositeSubscription()
        if (bookmarkList.isEmpty()) {
            initializeListContents()
        } else {
            if (bookmarkCommentFilter == BookmarkCommentFilter.COMMENT) {
                view.showUserList(bookmarkList.filter { bookmark -> bookmark.description.isNotEmpty() })
            } else {
                view.showUserList(bookmarkList)
            }
        }
    }

    override fun onPause() {
        subscription?.unsubscribe()
        subscription = null
    }

    private fun initializeListContents() {

        if (isLoading) return

        subscription?.let {
            view.showProgress()
            it.add(request())
        }
    }

    override fun onRefreshList() {

        if (isLoading) return

        subscription?.add(request())
    }

    override fun onOptionItemSelected(bookmarkCommentFilter: BookmarkCommentFilter) {

        if (this.bookmarkCommentFilter == bookmarkCommentFilter) return

        this.bookmarkCommentFilter = bookmarkCommentFilter

        if (bookmarkCommentFilter == BookmarkCommentFilter.COMMENT) {
            view.showUserList(bookmarkList.filter { bookmark -> bookmark.description.isNotEmpty() })
        } else {
            view.showUserList(bookmarkList)
        }
    }

    private fun request(): Subscription? {

        return getBookmarkedUsersUsecase.get(bookmarkEntity)
                .doOnSubscribe {
                    isLoading = true
                }
                .doOnUnsubscribe {
                    isLoading = false
                    view.hideProgress()
                }
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onFindByArticleUrlSuccess(it)
                }, {
                    onFindByArticleUrlFailure(it)
                })
    }

    private fun onFindByArticleUrlSuccess(bookmarkList: List<BookmarkEntity>) {

        this.bookmarkList = bookmarkList

        if (this.bookmarkList.isEmpty()) {
            view.hideUserList()
            view.showEmpty()
        } else {
            view.hideEmpty()
            if (bookmarkCommentFilter == BookmarkCommentFilter.COMMENT) {
                view.showUserList(bookmarkList.filter { bookmark -> bookmark.description.isNotEmpty() })
            } else {
                view.showUserList(bookmarkList)
            }
        }
    }

    private fun onFindByArticleUrlFailure(@Suppress("unused") e: Throwable) {
        view.showNetworkErrorMessage()
    }

    override fun onClickUser(bookmarkEntity: BookmarkEntity) {
        view.navigateToOthersBookmark(bookmarkEntity)
    }
}
