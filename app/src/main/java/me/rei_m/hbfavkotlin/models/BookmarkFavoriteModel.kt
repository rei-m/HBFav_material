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
                EventBusHolder.EVENT_BUS.post(BookmarkFavoriteLoadedEvent(EventType.COMPLETE))
            }

            override fun onError(e: Throwable?) {
                EventBusHolder.EVENT_BUS.post(BookmarkFavoriteLoadedEvent(EventType.ERROR))
            }
        }

        BookmarkFavoriteRss.request(startIndex)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo({
                    isBusy = false
                })
                .subscribe(observer)
    }
}