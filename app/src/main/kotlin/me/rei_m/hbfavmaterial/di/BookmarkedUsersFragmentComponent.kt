package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkedUsersFragment

@Subcomponent(modules = arrayOf(BookmarkedUsersFragmentModule::class))
interface BookmarkedUsersFragmentComponent {
    fun inject(fragment: BookmarkedUsersFragment)
}
