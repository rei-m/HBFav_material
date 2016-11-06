package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.fragment.SettingFragment

@Subcomponent(modules = arrayOf(SettingFragmentModule::class))
interface SettingFragmentComponent {
    fun inject(fragment: SettingFragment)
}
