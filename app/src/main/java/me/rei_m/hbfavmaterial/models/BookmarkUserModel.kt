package me.rei_m.hbfavmaterial.models

import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.network.BookmarkUserLoadedEvent
import me.rei_m.hbfavmaterial.events.network.LoadedEventStatus
import me.rei_m.hbfavmaterial.repositories.BookmarkRepository
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * ユーザーのブックマーク情報を管理するModel.
 */
class BookmarkUserModel {

    private val bookmarkRepository = BookmarkRepository()

    private var userId = ""

    var isBusy = false
        private set

    val bookmarkList = ArrayList<BookmarkEntity>()

    fun isSameUser(userId: String): Boolean = (this.userId == userId)

    fun fetch(userId: String, startIndex: Int = 0) {

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

        var isEmpty = false

        val observer = object : Observer<List<BookmarkEntity>> {
            override fun onNext(t: List<BookmarkEntity>?) {
                t ?: return
                if (startIndex === 0) {
                    bookmarkList.clear()
                }
                isEmpty = t.isEmpty()
                bookmarkList.addAll(t)
            }

            override fun onCompleted() {
                if (requestIndex === 0) {
                    bookmarkList.clear()
                }

                if (isEmpty) {
                    EventBusHolder.EVENT_BUS.post(BookmarkUserLoadedEvent(LoadedEventStatus.NOT_FOUND))
                } else {
                    EventBusHolder.EVENT_BUS.post(BookmarkUserLoadedEvent(LoadedEventStatus.OK))
                }
            }

            override fun onError(e: Throwable?) {
                EventBusHolder.EVENT_BUS.post(BookmarkUserLoadedEvent(LoadedEventStatus.ERROR))
            }
        }

        bookmarkRepository.findByUserId(userId, requestIndex)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo { isBusy = false }
                .subscribe(observer)
    }
}
