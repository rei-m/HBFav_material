package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent

@Subcomponent(modules = arrayOf(TestFragmentModule::class))
interface TestFragmentComponent : FragmentComponent
