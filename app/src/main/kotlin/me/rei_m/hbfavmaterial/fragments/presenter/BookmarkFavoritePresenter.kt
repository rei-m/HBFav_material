package me.rei_m.hbfavmaterial.fragments.presenter

import android.support.v4.app.Fragment
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.activities.BookmarkActivity
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.models.UserModel
import me.rei_m.hbfavmaterial.service.BookmarkService
import me.rei_m.hbfavmaterial.service.impl.BookmarkServiceImpl
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class BookmarkFavoritePresenter(private val view: BookmarkFavoriteContact.View) : BookmarkFavoriteContact.Actions {

    @Inject
    lateinit var userModel: UserModel

    //    @Inject
    lateinit var bookmarkService: BookmarkService

    private lateinit var bookmarkList: MutableList<BookmarkEntity>

    private var isLoading = false

    init {
        App.graph.inject(this)
    }

    override fun prepare() {
        bookmarkService = BookmarkServiceImpl()
        bookmarkList = ArrayList()
        isLoading = false
    }

    override fun fetchListContents(nextIndex: Int): Subscription? {

        // TODO
        val userId = userModel.userEntity?.id!!

        if (isLoading) return null

        isLoading = true

        val observer = object : Observer<List<BookmarkEntity>> {
            override fun onNext(t: List<BookmarkEntity>?) {
                t ?: return

                if (t.isEmpty()) {
                    view.stopAutoLoading()
                    return
                }
                if (nextIndex === 0) {
                    bookmarkList.clear()
                }
                bookmarkList.addAll(t)

                view.showBookmarkList(bookmarkList)
            }

            override fun onCompleted() {
            }

            override fun onError(e: Throwable?) {
                view.showNetworkErrorMessage()
            }
        }

        return bookmarkService.findByUserIdForFavorite(userId, nextIndex)
                .doOnSubscribe { isLoading = false }
                .doOnUnsubscribe { isLoading = false }
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
