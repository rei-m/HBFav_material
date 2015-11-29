package me.rei_m.hbfavkotlin.models

import me.rei_m.hbfavkotlin.entities.BookmarkEntity
import me.rei_m.hbfavkotlin.events.EventBusHolder
import me.rei_m.hbfavkotlin.events.UserRegisterBookmarkLoadedEvent
import me.rei_m.hbfavkotlin.network.EntryApi
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

public class UserRegisterBookmarkModel {

    public var isBusy = false
        private set

    public val bookmarkList = ArrayList<BookmarkEntity>()

    private var bookmarkUrl: String = ""

    public fun isSameUrl(bookmarkUrl: String): Boolean = (this.bookmarkUrl == bookmarkUrl)

    public fun fetch(bookmarkUrl: String) {

        if (this.bookmarkUrl != bookmarkUrl) {
            this.bookmarkUrl = bookmarkUrl
            bookmarkList.clear()
            isBusy = false
        }

        if (isBusy) {
            return
        }

        isBusy = true

        val listFromApi = ArrayList<BookmarkEntity>()

        val observer = object : Observer<BookmarkEntity> {
            override fun onNext(t: BookmarkEntity?) {
                listFromApi.add(t!!)
            }

            override fun onCompleted() {
                bookmarkList.clear()
                bookmarkList.addAll(listFromApi)
                EventBusHolder.EVENT_BUS.post(UserRegisterBookmarkLoadedEvent(UserRegisterBookmarkLoadedEvent.Companion.Type.COMPLETE))
            }

            override fun onError(e: Throwable?) {
                EventBusHolder.EVENT_BUS.post(UserRegisterBookmarkLoadedEvent(UserRegisterBookmarkLoadedEvent.Companion.Type.ERROR))
            }
        }

        EntryApi.request(bookmarkUrl)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo({
                    isBusy = false
                })
                .subscribe(observer)
    }
}