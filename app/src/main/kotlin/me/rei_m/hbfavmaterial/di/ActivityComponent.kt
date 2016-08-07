package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.activitiy.BaseDrawerActivity
import me.rei_m.hbfavmaterial.activitiy.BookmarkActivity
import me.rei_m.hbfavmaterial.activitiy.MainActivity
import me.rei_m.hbfavmaterial.activitiy.OAuthActivity
import me.rei_m.hbfavmaterial.fragment.EditBookmarkDialogFragment
import me.rei_m.hbfavmaterial.fragment.presenter.EditBookmarkDialogPresenter
import me.rei_m.hbfavmaterial.fragment.presenter.EditUserIdDialogPresenter

@Subcomponent(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {

    fun inject(baseActivityWithDrawer: BaseDrawerActivity)

    fun inject(bookmarkActivity: BookmarkActivity)

    fun inject(mainActivity: MainActivity)

    fun inject(oAuthActivity: OAuthActivity)

    fun inject(presenter: EditBookmarkDialogFragment)

    fun inject(presenter: EditBookmarkDialogPresenter)

    fun inject(presenter: EditUserIdDialogPresenter)

    fun plus(module: FragmentModule): FragmentComponent
}
