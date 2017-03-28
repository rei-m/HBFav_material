package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkFavoriteFragment
import me.rei_m.hbfavmaterial.presentation.view.adapter.BookmarkListAdapterr

@Subcomponent(modules = arrayOf(BookmarkFavoriteFragmentModule::class))
interface BookmarkFavoriteFragmentComponent : BookmarkListAdapterr.Injector {
    fun inject(fragment: BookmarkFavoriteFragment)
}
