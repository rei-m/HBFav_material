package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.activity.MainActivity
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkFavoriteFragment
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkUserFragment
import me.rei_m.hbfavmaterial.presentation.fragment.HotEntryFragment
import me.rei_m.hbfavmaterial.presentation.fragment.NewEntryFragment

@Subcomponent(modules = arrayOf(ActivityModule::class, MainActivityModule::class))
interface MainActivityComponent : BookmarkFavoriteFragment.Injector,
        BookmarkUserFragment.Injector,
        HotEntryFragment.Injector,
        NewEntryFragment.Injector {
    fun inject(activity: MainActivity)
}
