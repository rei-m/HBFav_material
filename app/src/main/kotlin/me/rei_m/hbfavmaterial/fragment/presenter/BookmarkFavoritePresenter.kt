package me.rei_m.hbfavmaterial.fragment.presenter

import android.support.v4.app.Fragment
import me.rei_m.hbfavmaterial.entitiy.BookmarkEntity
import me.rei_m.hbfavmaterial.fragment.BaseFragment
import me.rei_m.hbfavmaterial.manager.ActivityNavigator
import me.rei_m.hbfavmaterial.repository.UserRepository
import me.rei_m.hbfavmaterial.service.BookmarkService
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class BookmarkFavoritePresenter(private val view: BookmarkFavoriteContact.View) : BookmarkFavoriteContact.Actions {

    @Inject
    lateinit var navigator: ActivityNavigator

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var bookmarkService: BookmarkService

    private val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()

    private var isLoading = false

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

    private fun request(nextIndex: Int): Subscription? {

        val userId = userRepository.resolve().id

        return bookmarkService.findByUserIdForFavorite(userId, nextIndex)
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

    private fun onFindByUserIdForFavoriteFailure(e: Throwable) {
        view.showNetworkErrorMessage()
    }

    override fun clickBookmark(bookmarkEntity: BookmarkEntity) {
        val activity = (view as Fragment).activity
        navigator.navigateToBookmark(activity, bookmarkEntity)
    }
}
