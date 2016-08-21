package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent

@Subcomponent(modules = arrayOf(ActivityModule::class))
interface TestActivityComponent : ActivityComponent {
    override fun fragmentComponent(): TestFragmentComponent
}