package me.rei_m.hbfavmaterial.models

import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.network.LoadedEventStatus
import me.rei_m.hbfavmaterial.events.network.UserRegisterBookmarkLoadedEvent
import me.rei_m.hbfavmaterial.network.EntryApi
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * ブックマークしているユーザーを管理するModel.
 */
class UserRegisterBookmarkModel {

    public var isBusy = false
        private set

    public val bookmarkList = ArrayList<BookmarkEntity>()

    private var bookmarkUrl: String = ""

    /**
     * Model内で保持しているURLと指定されたURLが同じか判定する.
     */
    public fun isSameUrl(bookmarkUrl: String): Boolean = (this.bookmarkUrl == bookmarkUrl)

    public fun fetch(bookmarkUrl: String) {

        if (!isSameUrl(bookmarkUrl)) {
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
                EventBusHolder.EVENT_BUS.post(UserRegisterBookmarkLoadedEvent(LoadedEventStatus.OK))
            }

            override fun onError(e: Throwable?) {
                EventBusHolder.EVENT_BUS.post(UserRegisterBookmarkLoadedEvent(LoadedEventStatus.ERROR))
            }
        }

        EntryApi().request(bookmarkUrl)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo { isBusy = false }
                .subscribe(observer)
    }
}
