package me.rei_m.hbfavmaterial.models

import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.network.BookmarkUserLoadedEvent
import me.rei_m.hbfavmaterial.events.network.LoadedEventStatus
import me.rei_m.hbfavmaterial.network.BookmarkOwnRss
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * ユーザーのブックマーク情報を管理するModel.
 */
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

                if (listFromRss.isEmpty()) {
                    EventBusHolder.EVENT_BUS.post(BookmarkUserLoadedEvent(LoadedEventStatus.NOT_FOUND))
                } else {
                    bookmarkList.addAll(listFromRss)
                    EventBusHolder.EVENT_BUS.post(BookmarkUserLoadedEvent(LoadedEventStatus.OK))
                }
            }

            override fun onError(e: Throwable?) {
                EventBusHolder.EVENT_BUS.post(BookmarkUserLoadedEvent(LoadedEventStatus.ERROR))
            }
        }

        BookmarkOwnRss().request(userId, requestIndex)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo { isBusy = false }
                .subscribe(observer)
    }
}
