package me.rei_m.hbfavmaterial.presentation.fragment.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkedUsersFragment
import me.rei_m.hbfavmaterial.presentation.widget.adapter.UserListAdapter

@Subcomponent(modules = arrayOf(BookmarkedUsersFragmentModule::class))
interface BookmarkedUsersFragmentComponent : UserListAdapter.Injector {
    fun inject(fragment: BookmarkedUsersFragment)
}
