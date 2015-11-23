package me.rei_m.hbfavkotlin.models

import me.rei_m.hbfavkotlin.entities.BookmarkEntity
import me.rei_m.hbfavkotlin.events.BookmarkOwnLoadedEvent
import me.rei_m.hbfavkotlin.events.EventBusHolder
import me.rei_m.hbfavkotlin.network.BookmarkOwnRss
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import me.rei_m.hbfavkotlin.events.BookmarkOwnLoadedEvent.Companion.Type as EventType

public class BookmarkOwnModel {

    public var isBusy = false
        private set

    public val bookmarkList = ArrayList<BookmarkEntity>()

    public fun fetch(startIndex: Int = 0) {

        if (isBusy) {
            return
        }

        isBusy = true

        if (startIndex === 0) {
            bookmarkList.clear()
        }

        val observer = object : Observer<BookmarkEntity> {
            override fun onNext(t: BookmarkEntity?) {
                bookmarkList.add(t!!)
            }

            override fun onCompleted() {
                EventBusHolder.EVENT_BUS.post(BookmarkOwnLoadedEvent(EventType.COMPLETE))
            }

            override fun onError(e: Throwable?) {
                EventBusHolder.EVENT_BUS.post(BookmarkOwnLoadedEvent(EventType.ERROR))
            }
        }

        BookmarkOwnRss().request(startIndex)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo({
                    isBusy = false
                })
                .subscribe(observer)
    }
}