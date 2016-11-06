package me.rei_m.hbfavmaterial.di

import dagger.Component
import me.rei_m.hbfavmaterial.App
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class, InfraLayerModule::class))
interface ApplicationComponent {

    fun inject(application: App)

    fun plus(activityModule: BookmarkedUsersActivityModule): BookmarkedUsersActivityComponent

    fun plus(activityModule: BookmarkActivityModule): BookmarkActivityComponent

    fun plus(activityModule: ExplainAppActivityModule): ExplainAppActivityComponent

    fun plus(activityModule: MainActivityModule): MainActivityComponent

    fun plus(activityModule: OAuthActivityModule): OAuthActivityComponent

    fun plus(activityModule: SettingActivityModule): SettingActivityComponent

    fun plus(activityModule: SplashActivityModule): SplashActivityComponent
}
