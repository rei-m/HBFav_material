package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkFavoriteFragment
import me.rei_m.hbfavmaterial.presentation.view.adapter.BookmarkListAdapter

@Subcomponent(modules = arrayOf(BookmarkFavoriteFragmentModule::class))
interface BookmarkFavoriteFragmentComponent : BookmarkListAdapter.Injector {
    fun inject(fragment: BookmarkFavoriteFragment)
}
