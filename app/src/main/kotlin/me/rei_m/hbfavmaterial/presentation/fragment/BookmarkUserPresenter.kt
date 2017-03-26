package me.rei_m.hbfavmaterial.presentation.fragment

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.rei_m.hbfavmaterial.constant.ReadAfterFilter
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.usecase.GetUserBookmarksUsecase

class BookmarkUserPresenter(private val getUserBookmarksUsecase: GetUserBookmarksUsecase) : BookmarkUserContact.Actions {

    private lateinit var view: BookmarkUserContact.View

    private var isOwner: Boolean = false

    private var bookmarkUserId: String = ""

    private var disposable: CompositeDisposable? = null

    private var bookmarkList: List<BookmarkEntity> = listOf()

    private var isLoading = false

    override var readAfterFilter = ReadAfterFilter.ALL

    override fun onCreate(view: BookmarkUserContact.View,
                          isOwner: Boolean,
                          bookmarkUserId: String,
                          readAfterFilter: ReadAfterFilter) {
        this.view = view
        this.isOwner = isOwner
        this.bookmarkUserId = bookmarkUserId
        this.readAfterFilter = readAfterFilter
    }

    override fun onResume() {
        disposable = CompositeDisposable()
        if (bookmarkList.isEmpty()) {
            initializeListContents()
        } else {
            view.showBookmarkList(bookmarkList)
        }
    }

    override fun onPause() {
        disposable?.dispose()
        disposable = null
    }

    override fun onRefreshList() {
        if (isLoading) return

        disposable?.add(request(0))
    }

    override fun onScrollEnd(nextIndex: Int) {
        if (isLoading) return

        disposable?.add(request(nextIndex))
    }

    private fun initializeListContents() {

        if (isLoading) return

        disposable?.let {
            view.showProgress()
            it.add(request(0))
        }
    }

    override fun onOptionItemSelected(readAfterFilter: ReadAfterFilter) {

        if (isLoading) return

        if (this.readAfterFilter == readAfterFilter) return

        disposable?.let {

            this.readAfterFilter = readAfterFilter

            view.stopAutoLoading()

            view.hideBookmarkList()

            view.showProgress()

            it.add(request(0))
        }
    }

    private fun request(nextIndex: Int): Disposable? {

        val bookmarkObservable = if (isOwner) {
            getUserBookmarksUsecase.get(readAfterFilter, nextIndex)
        } else {
            getUserBookmarksUsecase.get(bookmarkUserId, readAfterFilter, nextIndex)
        }

        isLoading = true

        return bookmarkObservable.subscribeAsync({
            onFindByUserIdSuccess(it, nextIndex)
        }, {
            onFindByUserIdFailure(it)
        }, {
            isLoading = false
            view.hideProgress()
        })
    }

    private fun onFindByUserIdSuccess(bookmarkList: List<BookmarkEntity>, nextIndex: Int) {
        if (nextIndex == 0) {
            this.bookmarkList = bookmarkList
        } else {
            val totalBookmarkList: MutableList<BookmarkEntity> = mutableListOf()
            totalBookmarkList.addAll(this.bookmarkList)
            totalBookmarkList.addAll(bookmarkList)
            this.bookmarkList = totalBookmarkList
        }

        if (this.bookmarkList.isEmpty()) {
            view.hideBookmarkList()
            view.showEmpty()
        } else {
            view.hideEmpty()
            view.showBookmarkList(this.bookmarkList)
        }

        if (bookmarkList.isEmpty()) {
            view.stopAutoLoading()
        } else {
            view.startAutoLoading()
        }
    }

    private fun onFindByUserIdFailure(@Suppress("UNUSED_PARAMETER") e: Throwable) {
        view.showNetworkErrorMessage()
    }

    override fun onClickBookmark(bookmarkEntity: BookmarkEntity) {
        view.navigateToBookmark(bookmarkEntity)
    }
}
