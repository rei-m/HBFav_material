package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.fragments.*
import me.rei_m.hbfavmaterial.fragments.presenter.BookmarkFavoritePresenter

@Subcomponent(modules = arrayOf(FragmentModule::class))
interface FragmentComponent {

    fun inject(presenter: BookmarkFavoritePresenter)

    fun inject(bookmarkFavoriteFragment: BookmarkFavoriteFragment)

    fun inject(bookmarkUserFragment: BookmarkUserFragment)

    fun inject(bookmarkUsersFragment: BookmarkUsersFragment)

    fun inject(editBookmarkDialogFragment: EditBookmarkDialogFragment)

    fun inject(editUserIdDialogFragment: EditUserIdDialogFragment)

    fun inject(hotEntryFragment: HotEntryFragment)

    fun inject(initializeFragment: InitializeFragment)

    fun inject(newEntryFragment: NewEntryFragment)

    fun inject(settingFragment: SettingFragment)
}
