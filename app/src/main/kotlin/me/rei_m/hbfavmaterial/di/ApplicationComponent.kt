package me.rei_m.hbfavmaterial.di

import dagger.Component
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.activities.BaseActivityWithDrawer
import me.rei_m.hbfavmaterial.activities.BookmarkActivity
import me.rei_m.hbfavmaterial.activities.MainActivity
import me.rei_m.hbfavmaterial.activities.OAuthActivity
import me.rei_m.hbfavmaterial.fragments.*
import me.rei_m.hbfavmaterial.fragments.presenter.BookmarkFavoritePresenter
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class, AppLayerModule::class, InfraLayerModule::class))
interface ApplicationComponent {

    fun inject(application: App)

    fun inject(presenter: BookmarkFavoritePresenter)

    fun inject(baseActivityWithDrawer: BaseActivityWithDrawer)

    fun inject(bookmarkActivity: BookmarkActivity)

    fun inject(mainActivity: MainActivity)

    fun inject(oAuthActivity: OAuthActivity)

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
