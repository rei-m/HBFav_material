package me.rei_m.hbfavmaterial.presentation.fragment.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkFragment

@Subcomponent(modules = arrayOf(BookmarkFragmentModule::class))
interface BookmarkFragmentComponent {
    fun inject(fragment: BookmarkFragment)
}
