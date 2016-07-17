package me.rei_m.hbfavmaterial.di

import dagger.Component
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.fragments.EditBookmarkDialogFragment
import me.rei_m.hbfavmaterial.fragments.EditUserIdDialogFragment
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class, AppLayerModule::class, InfraLayerModule::class))
interface ApplicationComponent {

    fun inject(application: App)

    fun inject(dialog: EditBookmarkDialogFragment)

    fun inject(dialog: EditUserIdDialogFragment)

    fun plus(module: ActivityModule): ActivityComponent
}
