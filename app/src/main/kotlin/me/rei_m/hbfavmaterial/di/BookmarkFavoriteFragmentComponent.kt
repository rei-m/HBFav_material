package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.fragment.FavoriteBookmarkFragment
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkListAdapter

@Subcomponent(modules = arrayOf(BookmarkFavoriteFragmentModule::class))
interface BookmarkFavoriteFragmentComponent : BookmarkListAdapter.Injector {
    fun inject(fragment: FavoriteBookmarkFragment)
}
