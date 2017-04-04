package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.activity.MainActivity
import me.rei_m.hbfavmaterial.presentation.fragment.FavoriteBookmarkFragment
import me.rei_m.hbfavmaterial.presentation.fragment.UserBookmarkFragment
import me.rei_m.hbfavmaterial.presentation.fragment.HotEntryFragment
import me.rei_m.hbfavmaterial.presentation.fragment.NewEntryFragment

@Subcomponent(modules = arrayOf(ActivityModule::class, MainActivityModule::class))
interface MainActivityComponent : FavoriteBookmarkFragment.Injector,
        UserBookmarkFragment.Injector,
        HotEntryFragment.Injector,
        NewEntryFragment.Injector {
    fun inject(activity: MainActivity)
}
