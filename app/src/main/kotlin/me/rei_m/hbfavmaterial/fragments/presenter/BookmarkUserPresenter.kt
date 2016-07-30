package me.rei_m.hbfavmaterial.fragments.presenter

import android.support.v4.app.Fragment
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.enums.ReadAfterFilter
import me.rei_m.hbfavmaterial.fragments.BaseFragment
import me.rei_m.hbfavmaterial.manager.ActivityNavigator
import me.rei_m.hbfavmaterial.service.BookmarkService
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class BookmarkUserPresenter(private val view: BookmarkUserContact.View,
                            private val bookmarkUserId: String) : BookmarkUserContact.Actions {

    @Inject
    lateinit var navigator: ActivityNavigator

    @Inject
    lateinit var bookmarkService: BookmarkService

    private val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()

    private var isLoading = false

    var readAfterFilter = ReadAfterFilter.ALL
        private set

    init {
        (view as BaseFragment).component.inject(this)
    }

    override fun initializeListContents(): Subscription? {

        if (isLoading) return null

        view.showProgress()

        return request(0)
    }

    override fun fetchListContents(nextIndex: Int): Subscription? {

        if (isLoading) return null

        return request(nextIndex)
    }

    override fun toggleListContents(readAfterFilter: ReadAfterFilter): Subscription? {

        if (isLoading) return null

        if (this.readAfterFilter == readAfterFilter) return null

        this.readAfterFilter = readAfterFilter

        view.stopAutoLoading()

        view.hideBookmarkList()

        view.showProgress()

        return request(0)
    }

    private fun request(nextIndex: Int): Subscription? {

        isLoading = true

        return bookmarkService.findByUserId(bookmarkUserId, readAfterFilter, nextIndex)
                .doOnUnsubscribe {
                    isLoading = false
                    view.hideProgress()
                }
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onFindByUserIdSuccess(it, nextIndex)
                }, {
                    onFindByUserIdFailure(it)
                })
    }

    private fun onFindByUserIdSuccess(bookmarkList: List<BookmarkEntity>, nextIndex: Int) {
        if (nextIndex === 0) {
            this.bookmarkList.clear()
        }
        this.bookmarkList.addAll(bookmarkList)

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

    private fun onFindByUserIdFailure(e: Throwable) {
        view.showNetworkErrorMessage()
    }

    override fun clickBookmark(bookmarkEntity: BookmarkEntity) {
        val activity = (view as Fragment).activity
        navigator.navigateToBookmark(activity, bookmarkEntity)
    }
}
