package me.rei_m.hbfavmaterial.fragment.presenter

import me.rei_m.hbfavmaterial.entitiy.BookmarkEntity
import rx.Subscription

interface BookmarkFavoriteContact {

    interface View {

        fun showBookmarkList(bookmarkList: List<BookmarkEntity>)

        fun hideBookmarkList()

        fun showNetworkErrorMessage()

        fun showProgress()

        fun hideProgress()

        fun startAutoLoading()

        fun stopAutoLoading()

        fun showEmpty()

        fun hideEmpty()
    }

    interface Actions {

        fun initializeListContents(): Subscription?

        fun fetchListContents(nextIndex: Int): Subscription?

        fun clickBookmark(bookmarkEntity: BookmarkEntity)
    }
}
