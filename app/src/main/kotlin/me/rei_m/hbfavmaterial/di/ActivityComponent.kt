package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.activitiy.BaseDrawerActivity
import me.rei_m.hbfavmaterial.activitiy.BookmarkActivity
import me.rei_m.hbfavmaterial.activitiy.MainActivity
import me.rei_m.hbfavmaterial.activitiy.OAuthActivity
import me.rei_m.hbfavmaterial.fragments.EditBookmarkDialogFragment
import me.rei_m.hbfavmaterial.fragments.EditUserIdDialogFragment

@Subcomponent(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {

    fun inject(baseActivityWithDrawer: BaseDrawerActivity)

    fun inject(bookmarkActivity: BookmarkActivity)

    fun inject(mainActivity: MainActivity)

    fun inject(oAuthActivity: OAuthActivity)

    fun inject(dialog: EditBookmarkDialogFragment)

    fun inject(dialog: EditUserIdDialogFragment)

    fun plus(module: FragmentModule): FragmentComponent
}
