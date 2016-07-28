package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.fragments.*
import me.rei_m.hbfavmaterial.fragments.presenter.*

@Subcomponent(modules = arrayOf(FragmentModule::class))
interface FragmentComponent {

    fun inject(presenter: BookmarkFavoritePresenter)

    fun inject(presenter: BookmarkUserPresenter)

    fun inject(presenter: HotEntryPresenter)

    fun inject(presenter: NewEntryPresenter)

    fun inject(presenter: BookmarkedUsersPresenter)

    fun inject(bookmarkFavoriteFragment: BookmarkFavoriteFragment)

    fun inject(bookmarkUserFragment: BookmarkUserFragment)

    fun inject(bookmarkedUsersFragment: BookmarkedUsersFragment)

    fun inject(editBookmarkDialogFragment: EditBookmarkDialogFragment)

    fun inject(presenter: EditBookmarkDialogPresenter)

    fun inject(editUserIdDialogFragment: EditUserIdDialogFragment)

    fun inject(hotEntryFragment: HotEntryFragment)

    fun inject(initializeFragment: InitializeFragment)

    fun inject(newEntryFragment: NewEntryFragment)

    fun inject(settingFragment: SettingFragment)

    fun inject(explainAppFragment: ExplainAppFragment)
}
