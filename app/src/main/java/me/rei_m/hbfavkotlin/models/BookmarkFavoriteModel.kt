package me.rei_m.hbfavkotlin.models

import me.rei_m.hbfavkotlin.entities.BookmarkEntity
import me.rei_m.hbfavkotlin.events.BookmarkFavoriteLoadedEvent
import me.rei_m.hbfavkotlin.events.EventBusHolder
import me.rei_m.hbfavkotlin.network.BookmarkFavoriteRss
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import me.rei_m.hbfavkotlin.events.BookmarkFavoriteLoadedEvent.Companion.Type as EventType

public class BookmarkFavoriteModel {

    private var userId = ""

    public var isBusy = false
        private set

    public val bookmarkList = ArrayList<BookmarkEntity>()

    public fun fetch(userId: String, startIndex: Int = 0) {

        if (isBusy) {
            return
        }

        val requestIndex: Int
        if (this.userId != userId) {
            this.userId = userId
            bookmarkList.clear()
            requestIndex = 0
        } else {
            requestIndex = startIndex
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

                EventBusHolder.EVENT_BUS.post(BookmarkFavoriteLoadedEvent(EventType.COMPLETE))
            }

            override fun onError(e: Throwable?) {
                EventBusHolder.EVENT_BUS.post(BookmarkFavoriteLoadedEvent(EventType.ERROR))
            }
        }

        BookmarkFavoriteRss().request(userId, requestIndex)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo({
                    isBusy = false
                })
                .subscribe(observer)
    }
}