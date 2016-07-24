package me.rei_m.hbfavmaterial.di

import dagger.Component
import me.rei_m.hbfavmaterial.App
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class, InfraLayerModule::class))
interface ApplicationComponent {

    fun inject(application: App)

    fun plus(module: ActivityModule): ActivityComponent
}
