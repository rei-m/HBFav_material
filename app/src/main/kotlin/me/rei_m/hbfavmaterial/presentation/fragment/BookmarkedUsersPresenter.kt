package me.rei_m.hbfavmaterial.presentation.fragment

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.rei_m.hbfavmaterial.constant.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.usecase.GetBookmarkedUsersUsecase

class BookmarkedUsersPresenter(private val getBookmarkedUsersUsecase: GetBookmarkedUsersUsecase) : BookmarkedUsersContact.Actions {

    private lateinit var view: BookmarkedUsersContact.View

    private lateinit var bookmarkEntity: BookmarkEntity

    private var bookmarkList: List<BookmarkEntity> = listOf()

    private var disposable: CompositeDisposable? = null

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
        disposable = CompositeDisposable()
        if (bookmarkList.isEmpty()) {
            initializeListContents()
        } else {
            if (bookmarkCommentFilter == BookmarkCommentFilter.COMMENT) {
                view.showUserList(bookmarkList.filter { (_, description) -> description.isNotEmpty() })
            } else {
                view.showUserList(bookmarkList)
            }
        }
    }

    override fun onPause() {
        disposable?.dispose()
        disposable = null
    }

    private fun initializeListContents() {

        if (isLoading) return

        disposable?.let {
            view.showProgress()
            it.add(request())
        }
    }

    override fun onRefreshList() {

        if (isLoading) return

        disposable?.add(request())
    }

    override fun onOptionItemSelected(bookmarkCommentFilter: BookmarkCommentFilter) {

        if (this.bookmarkCommentFilter == bookmarkCommentFilter) return

        this.bookmarkCommentFilter = bookmarkCommentFilter

        if (bookmarkCommentFilter == BookmarkCommentFilter.COMMENT) {
            view.showUserList(bookmarkList.filter { (_, description) -> description.isNotEmpty() })
        } else {
            view.showUserList(bookmarkList)
        }
    }

    private fun request(): Disposable? {

        isLoading = true

        return getBookmarkedUsersUsecase.get(bookmarkEntity).subscribeAsync({
            onFindByArticleUrlSuccess(it)
        }, {
            onFindByArticleUrlFailure(it)
        }, {
            isLoading = false
            view.hideProgress()
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
                view.showUserList(bookmarkList.filter { (_, description) -> description.isNotEmpty() })
            } else {
                view.showUserList(bookmarkList)
            }
        }
    }

    private fun onFindByArticleUrlFailure(@Suppress("UNUSED_PARAMETER") e: Throwable) {
        view.showNetworkErrorMessage()
    }

    override fun onClickUser(bookmarkEntity: BookmarkEntity) {
        view.navigateToOthersBookmark(bookmarkEntity)
    }
}
