package me.rei_m.hbfavmaterial.models

import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.network.LoadedEventStatus
import me.rei_m.hbfavmaterial.events.network.UserRegisterBookmarkLoadedEvent
import me.rei_m.hbfavmaterial.repositories.BookmarkRepository
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ブックマークしているユーザーを管理するModel.
 */
@Singleton
class UserRegisterBookmarkModel @Inject constructor(private val bookmarkRepository: BookmarkRepository) {

    var isBusy = false
        private set

    val bookmarkList = ArrayList<BookmarkEntity>()

    private var bookmarkUrl: String = ""

    /**
     * Model内で保持しているURLと指定されたURLが同じか判定する.
     */
    fun isSameUrl(bookmarkUrl: String): Boolean = (this.bookmarkUrl == bookmarkUrl)

    fun fetch(bookmarkUrl: String) {

        if (!isSameUrl(bookmarkUrl)) {
            this.bookmarkUrl = bookmarkUrl
            bookmarkList.clear()
            isBusy = false
        }

        if (isBusy) {
            return
        }

        isBusy = true

        val observer = object : Observer<List<BookmarkEntity>> {
            override fun onNext(t: List<BookmarkEntity>?) {
                t ?: return
                bookmarkList.clear()
                bookmarkList.addAll(t)
            }

            override fun onCompleted() {
                isBusy = false
                EventBusHolder.EVENT_BUS.post(UserRegisterBookmarkLoadedEvent(LoadedEventStatus.OK))
            }

            override fun onError(e: Throwable?) {
                isBusy = false
                EventBusHolder.EVENT_BUS.post(UserRegisterBookmarkLoadedEvent(LoadedEventStatus.ERROR))
            }
        }

        bookmarkRepository.findByArticleUrl(bookmarkUrl)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }
}
