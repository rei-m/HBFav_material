package me.rei_m.hbfavkotlin.models

import me.rei_m.hbfavkotlin.entities.BookmarkEntity
import me.rei_m.hbfavkotlin.events.BookmarkUserLoadedEvent
import me.rei_m.hbfavkotlin.events.EventBusHolder
import me.rei_m.hbfavkotlin.network.BookmarkOwnRss
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import me.rei_m.hbfavkotlin.events.BookmarkUserLoadedEvent.Companion.Type as EventType

public class BookmarkUserModel {

    private var userId = ""

    public var isBusy = false
        private set

    public val bookmarkList = ArrayList<BookmarkEntity>()

    public fun isSameUser(userId: String): Boolean = (this.userId == userId)

    public fun fetch(userId: String, startIndex: Int = 0) {

        val requestIndex: Int
        if (this.userId != userId) {
            this.userId = userId
            bookmarkList.clear()
            requestIndex = 0
            isBusy = false
        } else {
            requestIndex = startIndex
        }

        if (isBusy) {
            return
        }

        isBusy = true

        val listFromRss = ArrayList<BookmarkEntity>()

        val observer = object : Observer<BookmarkEntity> {
            override fun onNext(t: BookmarkEntity?) {
                listFromRss.add(t!!)
            }

            override fun onCompleted() {
                if (requestIndex === 0) {
                    bookmarkList.clear()
                }
                bookmarkList.addAll(listFromRss)
                EventBusHolder.EVENT_BUS.post(BookmarkUserLoadedEvent(EventType.COMPLETE))
            }

            override fun onError(e: Throwable?) {
                EventBusHolder.EVENT_BUS.post(BookmarkUserLoadedEvent(EventType.ERROR))
            }
        }

        BookmarkOwnRss().request(userId, requestIndex)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo({
                    isBusy = false
                })
                .subscribe(observer)
    }
}