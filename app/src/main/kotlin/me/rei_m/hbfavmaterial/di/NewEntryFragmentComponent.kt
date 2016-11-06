package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.fragment.NewEntryFragment

@Subcomponent(modules = arrayOf(NewEntryFragmentModule::class))
interface NewEntryFragmentComponent {
    fun inject(fragment: NewEntryFragment)
}
