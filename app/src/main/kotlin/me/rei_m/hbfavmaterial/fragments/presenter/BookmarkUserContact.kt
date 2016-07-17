package me.rei_m.hbfavmaterial.fragments.presenter

import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.enums.ReadAfterFilter
import rx.Subscription

interface BookmarkUserContact {

    interface View {

        fun showProgress()

        fun hideProgress()

        fun showBookmarkList(bookmarkList: List<BookmarkEntity>)

        fun hideBookmarkList()

        fun showNetworkErrorMessage()

        fun startAutoLoading()

        fun stopAutoLoading()

        fun showEmpty()

        fun hideEmpty()
    }

    interface Actions {

        fun initializeListContents(): Subscription?

        fun fetchListContents(nextIndex: Int): Subscription?

        fun toggleListContents(readAfterFilter: ReadAfterFilter): Subscription?

        fun clickBookmark(bookmarkEntity: BookmarkEntity)
    }
}
