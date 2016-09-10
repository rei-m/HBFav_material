package me.rei_m.hbfavmaterial.presentation.fragment

import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.usecase.GetFavoriteBookmarksUsecase
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

class BookmarkFavoritePresenter(private val getFavoriteBookmarksUsecase: GetFavoriteBookmarksUsecase) : BookmarkFavoriteContact.Actions {

    private lateinit var view: BookmarkFavoriteContact.View

    private var subscription: CompositeSubscription? = null

    private var bookmarkList: List<BookmarkEntity> = mutableListOf()

    private var isLoading = false

    override fun onCreate(view: BookmarkFavoriteContact.View) {
        this.view = view
    }

    override fun onResume() {
        subscription = CompositeSubscription()
        if (bookmarkList.isEmpty()) {
            initializeListContents()
        } else {
            view.showBookmarkList(bookmarkList)
        }
    }

    override fun onPause() {
        subscription?.unsubscribe()
        subscription = null
    }

    override fun onRefreshList() {

        if (isLoading) return

        subscription?.add(request(0))
    }

    override fun onScrollEnd(nextIndex: Int) {

        if (isLoading) return

        subscription?.add(request(nextIndex))
    }

    private fun initializeListContents() {

        if (isLoading) return

        subscription?.let {
            view.showProgress()
            it.add(request(0))
        }
    }

    private fun request(nextIndex: Int): Subscription? {

        return getFavoriteBookmarksUsecase.get(nextIndex)
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
                    onFindByUserIdForFavoriteSuccess(it, nextIndex)
                }, {
                    onFindByUserIdForFavoriteFailure(it)
                })
    }

    private fun onFindByUserIdForFavoriteSuccess(bookmarkList: List<BookmarkEntity>, nextIndex: Int) {
        if (nextIndex === 0) {
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

    private fun onFindByUserIdForFavoriteFailure(@Suppress("unused") e: Throwable) {
        view.showNetworkErrorMessage()
    }

    override fun onClickBookmark(bookmarkEntity: BookmarkEntity) {
        view.navigateToBookmark(bookmarkEntity)
    }
}
