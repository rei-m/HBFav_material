package me.rei_m.hbfavmaterial.models

import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.network.BookmarkFavoriteLoadedEvent
import me.rei_m.hbfavmaterial.events.network.LoadedEventStatus
import me.rei_m.hbfavmaterial.network.BookmarkFavoriteRss
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * お気に入りのBookmarkを管理するModel.
 */
public class BookmarkFavoriteModel {

    public var isBusy = false
        private set

    public val bookmarkList = ArrayList<BookmarkEntity>()

    public fun fetch(userId: String, startIndex: Int = 0) {

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
                if (startIndex === 0) {
                    bookmarkList.clear()
                }

                if (listFromRss.isEmpty()) {
                    EventBusHolder.EVENT_BUS.post(BookmarkFavoriteLoadedEvent(LoadedEventStatus.NOT_FOUND))
                } else {
                    bookmarkList.addAll(listFromRss)
                    EventBusHolder.EVENT_BUS.post(BookmarkFavoriteLoadedEvent(LoadedEventStatus.OK))
                }
            }

            override fun onError(e: Throwable?) {
                EventBusHolder.EVENT_BUS.post(BookmarkFavoriteLoadedEvent(LoadedEventStatus.ERROR))
            }
        }

        BookmarkFavoriteRss().request(userId, startIndex)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo { isBusy = false }
                .subscribe(observer)
    }
}
