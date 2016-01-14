package me.rei_m.hbfavmaterial.models

import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.network.BookmarkFavoriteLoadedEvent
import me.rei_m.hbfavmaterial.events.network.LoadedEventStatus
import me.rei_m.hbfavmaterial.repositories.BookmarkRepository
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * お気に入りのBookmarkを管理するModel.
 */
class BookmarkFavoriteModel {

    private val bookmarkRepository = BookmarkRepository()

    var isBusy = false
        private set

    val bookmarkList = ArrayList<BookmarkEntity>()

    fun fetch(userId: String, startIndex: Int = 0) {

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
                if (isEmpty) {
                    EventBusHolder.EVENT_BUS.post(BookmarkFavoriteLoadedEvent(LoadedEventStatus.NOT_FOUND))
                } else {
                    EventBusHolder.EVENT_BUS.post(BookmarkFavoriteLoadedEvent(LoadedEventStatus.OK))
                }
            }

            override fun onError(e: Throwable?) {
                EventBusHolder.EVENT_BUS.post(BookmarkFavoriteLoadedEvent(LoadedEventStatus.ERROR))
            }
        }

        bookmarkRepository.findByUserIdForFavorite(userId, startIndex)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo { isBusy = false }
                .subscribe(observer)
    }
}
