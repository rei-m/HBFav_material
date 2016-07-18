package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.fragments.*
import me.rei_m.hbfavmaterial.fragments.presenter.BookmarkFavoritePresenter
import me.rei_m.hbfavmaterial.fragments.presenter.BookmarkUserPresenter
import me.rei_m.hbfavmaterial.fragments.presenter.HotEntryPresenter

@Subcomponent(modules = arrayOf(FragmentModule::class))
interface FragmentComponent {

    fun inject(presenter: BookmarkFavoritePresenter)

    fun inject(presenter: BookmarkUserPresenter)

    fun inject(presenter: HotEntryPresenter)

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
