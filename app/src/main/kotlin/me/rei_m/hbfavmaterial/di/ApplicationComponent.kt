package me.rei_m.hbfavmaterial.di

import dagger.Component
import me.rei_m.hbfavmaterial.App
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class, InfraLayerModule::class))
interface ApplicationComponent {

    fun inject(application: App)

    fun plus(bookmarkedUsersActivityModule: BookmarkedUsersActivityModule, activityModule: ActivityModule): BookmarkedUsersActivityComponent

    fun plus(bookmarkActivityModule: BookmarkActivityModule?, activityModule: ActivityModule): BookmarkActivityComponent

    fun plus(explainAppActivityModule: ExplainAppActivityModule?, activityModule: ActivityModule): ExplainAppActivityComponent

    fun plus(mainActivityModule: MainActivityModule?, activityModule: ActivityModule): MainActivityComponent

    fun plus(oAuthActivityModule: OAuthActivityModule?, activityModule: ActivityModule): OAuthActivityComponent

    fun plus(othersActivityModule: OthersBookmarkActivityModule, activityModule: ActivityModule): OthersBookmarkActivityComponent

    fun plus(settingActivityModule: SettingActivityModule?, activityModule: ActivityModule): SettingActivityComponent

    fun plus(splashActivityModule: SplashActivityModule?, activityModule: ActivityModule): SplashActivityComponent
}
