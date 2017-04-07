package me.rei_m.hbfavmaterial.presentation.activity.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.activity.BookmarkedUsersActivity
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkedUsersFragment

@Subcomponent(modules = arrayOf(ActivityModule::class, BookmarkedUsersActivityModule::class))
interface BookmarkedUsersActivityComponent : BookmarkedUsersFragment.Injector {
    fun inject(activity: BookmarkedUsersActivity)
}
