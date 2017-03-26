package me.rei_m.hbfavmaterial.presentation.fragment

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.usecase.GetFavoriteBookmarksUsecase

class BookmarkFavoritePresenter(private val getFavoriteBookmarksUsecase: GetFavoriteBookmarksUsecase) : BookmarkFavoriteContact.Actions {

    private lateinit var view: BookmarkFavoriteContact.View

    private var dispoable: CompositeDisposable? = null

    private var bookmarkList: List<BookmarkEntity> = mutableListOf()

    private var isLoading = false

    override fun onCreate(view: BookmarkFavoriteContact.View) {
        this.view = view
    }

    override fun onResume() {
        dispoable = CompositeDisposable()
        if (bookmarkList.isEmpty()) {
            initializeListContents()
        } else {
            view.showBookmarkList(bookmarkList)
        }
    }

    override fun onPause() {
        dispoable?.dispose()
        dispoable = null
    }

    override fun onRefreshList() {

        if (isLoading) return

        dispoable?.add(request(0))
    }

    override fun onScrollEnd(nextIndex: Int) {

        if (isLoading) return

        dispoable?.add(request(nextIndex))
    }

    private fun initializeListContents() {

        if (isLoading) return

        dispoable?.let {
            view.showProgress()
            it.add(request(0))
        }
    }

    private fun request(nextIndex: Int): Disposable? {

        isLoading = true

        return getFavoriteBookmarksUsecase.get(nextIndex).subscribeAsync({
            onFindByUserIdForFavoriteSuccess(it, nextIndex)
        }, {
            onFindByUserIdForFavoriteFailure(it)
        }, {
            isLoading = false
            view.hideProgress()
        })
    }

    private fun onFindByUserIdForFavoriteSuccess(bookmarkList: List<BookmarkEntity>, nextIndex: Int) {
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

    private fun onFindByUserIdForFavoriteFailure(@Suppress("UNUSED_PARAMETER") e: Throwable) {
        view.showNetworkErrorMessage()
    }

    override fun onClickBookmark(bookmarkEntity: BookmarkEntity) {
        view.navigateToBookmark(bookmarkEntity)
    }
}
