package me.rei_m.hbfavmaterial.presentation.activity.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.activity.SplashActivity
import me.rei_m.hbfavmaterial.presentation.fragment.InitializeFragment

@Subcomponent(modules = arrayOf(ActivityModule::class, SplashActivityModule::class))
interface SplashActivityComponent : InitializeFragment.Injector {
    fun inject(activity: SplashActivity)
}
