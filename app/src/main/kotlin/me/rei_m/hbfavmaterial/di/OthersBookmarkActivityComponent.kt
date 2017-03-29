package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.activity.OthersBookmarkActivity
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkUserFragment

@Subcomponent(modules = arrayOf(ActivityModule::class, OthersBookmarkActivityModule::class))
interface OthersBookmarkActivityComponent : BookmarkUserFragment.Injector {
    fun inject(activity: OthersBookmarkActivity)
}
