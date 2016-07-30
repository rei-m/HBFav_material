package me.rei_m.hbfavmaterial.fragments.presenter

import android.support.v4.app.Fragment
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.enums.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.fragments.BaseFragment
import me.rei_m.hbfavmaterial.manager.ActivityNavigator
import me.rei_m.hbfavmaterial.service.BookmarkService
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class BookmarkedUsersPresenter(private val view: BookmarkedUsersContact.View,
                               private val bookmarkEntity: BookmarkEntity) : BookmarkedUsersContact.Actions {

    @Inject
    lateinit var navigator: ActivityNavigator

    @Inject
    lateinit var bookmarkService: BookmarkService

    private val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()

    private var isLoading = false

    var bookmarkCommentFilter: BookmarkCommentFilter = BookmarkCommentFilter.ALL
        private set

    init {
        (view as BaseFragment).component.inject(this)
    }

    override fun initializeListContents(): Subscription? {

        if (isLoading) return null

        view.showProgress()

        return request()
    }

    override fun fetchListContents(): Subscription? {

        if (isLoading) return null

        return request()
    }

    override fun toggleListContents(bookmarkCommentFilter: BookmarkCommentFilter) {
        if (bookmarkCommentFilter == BookmarkCommentFilter.COMMENT) {
            view.showUserList(bookmarkList.filter { bookmark -> bookmark.description.isNotEmpty() })
        } else {
            view.showUserList(bookmarkList)
        }
    }

    private fun request(): Subscription? {

        isLoading = true

        return bookmarkService.findByArticleUrl(bookmarkEntity.articleEntity.url)
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
        this.bookmarkList.clear()
        this.bookmarkList.addAll(bookmarkList)

        if (bookmarkList.isEmpty()) {
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

    private fun onFindByArticleUrlFailure(e: Throwable) {
        view.showNetworkErrorMessage()
    }

    override fun clickUser(bookmarkEntity: BookmarkEntity) {
        val activity = (view as Fragment).activity
        navigator.navigateToOthersBookmark(activity, bookmarkEntity.creator)
    }
}
