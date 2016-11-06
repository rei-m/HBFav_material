package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkFavoriteFragment

@Subcomponent(modules = arrayOf(BookmarkFavoriteFragmentModule::class))
interface BookmarkFavoriteFragmentComponent {
    fun inject(fragment: BookmarkFavoriteFragment)
}
