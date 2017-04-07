package me.rei_m.hbfavmaterial.presentation.fragment.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkedUsersFragment

@Subcomponent
interface ExplainAppFragmentComponent {
    fun inject(fragment: BookmarkedUsersFragment)
}
