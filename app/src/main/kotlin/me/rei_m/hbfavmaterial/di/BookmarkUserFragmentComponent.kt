package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkUserFragment

@Subcomponent(modules = arrayOf(BookmarkUserFragmentModule::class))
interface BookmarkUserFragmentComponent {
    fun inject(fragment: BookmarkUserFragment)
}
