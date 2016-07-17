package me.rei_m.hbfavmaterial.fragments.presenter

import android.support.v4.app.Fragment
import me.rei_m.hbfavmaterial.activities.BookmarkActivity
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.enums.ReadAfterFilter
import me.rei_m.hbfavmaterial.fragments.BaseFragment
import me.rei_m.hbfavmaterial.service.BookmarkService
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class BookmarkUserPresenter(private val view: BookmarkUserContact.View,
                            private val bookmarkUserId: String) : BookmarkUserContact.Actions {

    @Inject
    lateinit var bookmarkService: BookmarkService

    private val bookmarkList: MutableList<BookmarkEntity> = ArrayList()

    private var isLoading = false

    var readAfterFilter = ReadAfterFilter.ALL
        private set

    init {
        (view as BaseFragment).component.inject(this)
    }

    override fun initializeListContents(): Subscription? {

        if (isLoading) return null

        view.showProgress()

        return fetchListContents(0)
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

        val observer = object : Observer<List<BookmarkEntity>> {
            override fun onNext(t: List<BookmarkEntity>?) {
                t ?: return

                if (nextIndex === 0) {
                    bookmarkList.clear()
                }
                bookmarkList.addAll(t)

                if (bookmarkList.isEmpty()) {
                    view.hideBookmarkList()
                    view.showEmpty()
                } else {
                    view.hideEmpty()
                    view.showBookmarkList(bookmarkList)
                }

                if (t.isEmpty()) {
                    view.stopAutoLoading()
                } else {
                    view.startAutoLoading()
                }
            }

            override fun onCompleted() {
            }

            override fun onError(e: Throwable?) {
                view.showNetworkErrorMessage()
            }
        }

        return bookmarkService.findByUserId(bookmarkUserId, readAfterFilter, nextIndex)
                .doOnUnsubscribe {
                    isLoading = false
                    view.hideProgress()
                }
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }

    override fun clickBookmark(bookmarkEntity: BookmarkEntity) {
        val activity = (view as Fragment).activity
        activity.startActivity(BookmarkActivity.Companion.createIntent(activity, bookmarkEntity))
    }
}
