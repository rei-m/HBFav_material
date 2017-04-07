package me.rei_m.hbfavmaterial.presentation.activity.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.activity.OthersBookmarkActivity
import me.rei_m.hbfavmaterial.presentation.fragment.UserBookmarkFragment

@Subcomponent(modules = arrayOf(ActivityModule::class, OthersBookmarkActivityModule::class))
interface OthersBookmarkActivityComponent : UserBookmarkFragment.Injector {
    fun inject(activity: OthersBookmarkActivity)
}
