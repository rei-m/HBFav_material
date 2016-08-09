package me.rei_m.hbfavmaterial.fragment.presenter

import me.rei_m.hbfavmaterial.di.FragmentComponent
import me.rei_m.hbfavmaterial.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.enum.BookmarkCommentFilter

interface BookmarkedUsersContact {

    interface View {

        fun showUserList(bookmarkList: List<BookmarkEntity>)

        fun hideUserList()

        fun showNetworkErrorMessage()

        fun showProgress()

        fun hideProgress()

        fun showEmpty()

        fun hideEmpty()
    }

    interface Actions {

        fun onCreate(component: FragmentComponent,
                     view: BookmarkedUsersContact.View,
                     bookmarkEntity: BookmarkEntity)

        fun onResume()

        fun onPause()

        fun onClickUser(bookmarkEntity: BookmarkEntity)

        fun onRefreshList()

        fun onOptionItemSelected(bookmarkCommentFilter: BookmarkCommentFilter)
    }
}
