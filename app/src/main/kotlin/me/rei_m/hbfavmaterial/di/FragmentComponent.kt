package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.fragment.*
import me.rei_m.hbfavmaterial.fragment.presenter.*

@Subcomponent(modules = arrayOf(FragmentModule::class))
interface FragmentComponent {

    fun inject(presenter: BookmarkFavoritePresenter)

    fun inject(presenter: BookmarkUserPresenter)

    fun inject(presenter: HotEntryPresenter)

    fun inject(presenter: NewEntryPresenter)

    fun inject(presenter: BookmarkedUsersPresenter)

    fun inject(presenter: InitializePresenter)

    fun inject(presenter: SettingPresenter)

    fun inject(bookmarkFavoriteFragment: BookmarkFavoriteFragment)

    fun inject(bookmarkUserFragment: BookmarkUserFragment)

    fun inject(bookmarkedUsersFragment: BookmarkedUsersFragment)

    fun inject(editBookmarkDialogFragment: EditBookmarkDialogFragment)

    fun inject(editUserIdDialogFragment: EditUserIdDialogFragment)

    fun inject(initializeFragment: InitializeFragment)

    fun inject(newEntryFragment: NewEntryFragment)

    fun inject(settingFragment: SettingFragment)

    fun inject(explainAppFragment: ExplainAppFragment)
}
