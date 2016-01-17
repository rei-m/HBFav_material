package me.rei_m.hbfavmaterial.di

import dagger.Component
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.activities.*
import me.rei_m.hbfavmaterial.fragments.*
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class, AppLayerModule::class))
interface ApplicationComponent {

    fun inject(application: App)

    fun inject(baseActivityWithDrawer: BaseActivityWithDrawer)

    fun inject(bookmarkActivity: BookmarkActivity)

    fun inject(bookmarkUsersActivity: BookmarkUsersActivity)

    fun inject(explainAppActivity: ExplainAppActivity)

    fun inject(frameActivity: FrameActivity)

    fun inject(mainActivity: MainActivity)

    fun inject(oAuthActivity: OAuthActivity)

    fun inject(othersBookmarkActivity: OthersBookmarkActivity)

    fun inject(settingActivity: SettingActivity)

    fun inject(splashActivity: SplashActivity)

    fun inject(bookmarkFavoriteFragment: BookmarkFavoriteFragment)

    fun inject(bookmarkUserFragment: BookmarkUserFragment)

    fun inject(hotEntryFragment: HotEntryFragment)

    fun inject(newEntryFragment: NewEntryFragment)

    fun inject(bookmarkFragment: BookmarkFragment)

    fun inject(bookmarkUsersFragment: BookmarkUsersFragment)
}
