package me.rei_m.hbfavmaterial.fragments.presenter

import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import rx.Subscription

interface BookmarkFavoriteContact {

    interface View {

        fun showBookmarkList(bookmarkList: List<BookmarkEntity>)

        fun showNetworkErrorMessage()

        fun stopAutoLoading()
    }

    interface Actions {

        fun fetchListContents(nextIndex: Int): Subscription?

        fun clickBookmark(bookmarkEntity: BookmarkEntity)
    }
}
