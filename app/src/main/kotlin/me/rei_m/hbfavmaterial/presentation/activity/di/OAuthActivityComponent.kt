package me.rei_m.hbfavmaterial.presentation.activity.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.activity.OAuthActivity

@Subcomponent(modules = arrayOf(ActivityModule::class, OAuthActivityModule::class))
interface OAuthActivityComponent {
    fun inject(activity: OAuthActivity)
}
