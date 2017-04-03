package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.activity.BookmarkActivity
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkFragment
import me.rei_m.hbfavmaterial.presentation.fragment.EditBookmarkDialogFragment

@Subcomponent(modules = arrayOf(ActivityModule::class, BookmarkActivityModule::class))
interface BookmarkActivityComponent : BookmarkFragment.Injector,
        EditBookmarkDialogFragment.Injector {
    fun inject(activity: BookmarkActivity)
}
