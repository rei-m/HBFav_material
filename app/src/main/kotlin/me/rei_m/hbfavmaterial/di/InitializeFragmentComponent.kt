package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.fragment.InitializeFragment

@Subcomponent(modules = arrayOf(InitializeFragmentModule::class))
interface InitializeFragmentComponent {
    fun inject(fragment: InitializeFragment)
}
