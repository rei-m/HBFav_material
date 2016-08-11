package me.rei_m.hbfavmaterial.fragment.presenter

import android.support.v4.app.Fragment
import me.rei_m.hbfavmaterial.di.FragmentComponent
import me.rei_m.hbfavmaterial.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.enum.ReadAfterFilter
import me.rei_m.hbfavmaterial.manager.ActivityNavigator
import me.rei_m.hbfavmaterial.repository.UserRepository
import me.rei_m.hbfavmaterial.service.BookmarkService
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class BookmarkUserPresenter() : BookmarkUserContact.Actions {

    @Inject
    lateinit var navigator: ActivityNavigator

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var bookmarkService: BookmarkService

    private lateinit var view: BookmarkUserContact.View

    private lateinit var bookmarkUserId: String

    private var subscription: CompositeSubscription? = null

    private val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()

    private var isLoading = false

    override var readAfterFilter = ReadAfterFilter.ALL

    override fun onCreate(component: FragmentComponent,
                          view: BookmarkUserContact.View,
                          isOwner: Boolean,
                          bookmarkUserId: String,
                          readAfterFilter: ReadAfterFilter) {
        component.inject(this)
        this.view = view
        this.bookmarkUserId = if (isOwner)
            userRepository.resolve().id
        else
            bookmarkUserId
        this.readAfterFilter = readAfterFilter
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

    override fun onOptionItemSelected(readAfterFilter: ReadAfterFilter) {

        if (isLoading) return

        if (this.readAfterFilter == readAfterFilter) return

        subscription?.let {

            this.readAfterFilter = readAfterFilter

            view.stopAutoLoading()

            view.hideBookmarkList()

            view.showProgress()

            it.add(request(0))
        }
    }

    private fun request(nextIndex: Int): Subscription? {

        return bookmarkService.findByUserId(bookmarkUserId, readAfterFilter, nextIndex)
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

    override fun onClickBookmark(bookmarkEntity: BookmarkEntity) {
        val activity = (view as Fragment).activity
        navigator.navigateToBookmark(activity, bookmarkEntity)
    }
}
