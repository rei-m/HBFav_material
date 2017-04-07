package me.rei_m.hbfavmaterial.presentation.activity.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.activity.SettingActivity
import me.rei_m.hbfavmaterial.presentation.fragment.EditUserIdDialogFragment
import me.rei_m.hbfavmaterial.presentation.fragment.SettingFragment

@Subcomponent(modules = arrayOf(ActivityModule::class, SettingActivityModule::class))
interface SettingActivityComponent : SettingFragment.Injector, EditUserIdDialogFragment.Injector {
    fun inject(activity: SettingActivity)
}
