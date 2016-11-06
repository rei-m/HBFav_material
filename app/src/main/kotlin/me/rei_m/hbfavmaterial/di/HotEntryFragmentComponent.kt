package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.fragment.HotEntryFragment

@Subcomponent(modules = arrayOf(HotEntryFragmentModule::class))
interface HotEntryFragmentComponent {
    fun inject(fragment: HotEntryFragment)
}
