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

    fun inject(fragment: BookmarkFavoriteFragment)

    fun inject(fragment: BookmarkUserFragment)

    fun inject(fragment: BookmarkedUsersFragment)

    fun inject(fragment: EditBookmarkDialogFragment)

    fun inject(fragment: EditUserIdDialogFragment)

    fun inject(fragment: InitializeFragment)

    fun inject(fragment: NewEntryFragment)

    fun inject(fragment: HotEntryFragment)

    fun inject(fragment: SettingFragment)

    fun inject(fragment: ExplainAppFragment)
}
