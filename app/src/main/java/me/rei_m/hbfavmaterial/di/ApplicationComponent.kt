package me.rei_m.hbfavmaterial.di

import dagger.Component
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.activities.*
import me.rei_m.hbfavmaterial.fragments.*
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class, AppLayerModule::class, InfraLayerModule::class))
interface ApplicationComponent {

    fun inject(application: App)

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
