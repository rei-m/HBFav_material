package me.rei_m.hbfavmaterial.presentation.activity.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.activity.ExplainAppActivity
import me.rei_m.hbfavmaterial.presentation.fragment.ExplainAppFragment

@Subcomponent(modules = arrayOf(ActivityModule::class, ExplainAppActivityModule::class))
interface ExplainAppActivityComponent : ExplainAppFragment.Injector {
    fun inject(activity: ExplainAppActivity)
}
