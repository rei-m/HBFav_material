package me.rei_m.hbfavmaterial.di

import dagger.Module
import me.rei_m.hbfavmaterial.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.enum.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.fragment.presenter.BookmarkedUsersContact
import me.rei_m.hbfavmaterial.fragment.presenter.InitializeContact
import me.rei_m.hbfavmaterial.repository.UserRepository
import me.rei_m.hbfavmaterial.service.BookmarkService
import me.rei_m.hbfavmaterial.service.UserService

@Module
class TestFragmentModule : FragmentModule() {
    override fun createInitializePresenter(userRepository: UserRepository,
                                           userService: UserService): InitializeContact.Actions {
        return object : InitializeContact.Actions {
            override fun onCreate(view: InitializeContact.View) {
            }

            override fun onResume() {
            }

            override fun onPause() {
            }

            override fun onClickButtonSetId(userId: String) {
            }
        }
    }

    override fun createBookmarkedUsersPresenter(bookmarkService: BookmarkService): BookmarkedUsersContact.Actions {
        return object : BookmarkedUsersContact.Actions {
            override var bookmarkCommentFilter: BookmarkCommentFilter = BookmarkCommentFilter.ALL

            override fun onCreate(view: BookmarkedUsersContact.View,
                                  bookmarkEntity: BookmarkEntity,
                                  bookmarkCommentFilter: BookmarkCommentFilter) {
            }

            override fun onResume() {
            }

            override fun onPause() {
            }

            override fun onClickUser(bookmarkEntity: BookmarkEntity) {
            }

            override fun onRefreshList() {
            }

            override fun onOptionItemSelected(bookmarkCommentFilter: BookmarkCommentFilter) {
            }
        }
    }
}