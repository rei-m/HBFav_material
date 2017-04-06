package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.fragment.UserBookmarkFragment
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkListAdapter

@Subcomponent(modules = arrayOf(BookmarkUserFragmentModule::class))
interface BookmarkUserFragmentComponent : BookmarkListAdapter.Injector {
    fun inject(fragment: UserBookmarkFragment)
}
