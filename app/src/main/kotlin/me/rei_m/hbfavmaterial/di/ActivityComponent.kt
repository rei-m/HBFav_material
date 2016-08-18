package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.activity.BaseDrawerActivity
import me.rei_m.hbfavmaterial.activity.BookmarkActivity
import me.rei_m.hbfavmaterial.activity.MainActivity
import me.rei_m.hbfavmaterial.activity.OAuthActivity
import me.rei_m.hbfavmaterial.fragment.EditBookmarkDialogFragment
import me.rei_m.hbfavmaterial.fragment.EditUserIdDialogFragment
import me.rei_m.hbfavmaterial.fragment.presenter.EditUserIdDialogPresenter

@Subcomponent(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {

    fun inject(baseActivityWithDrawer: BaseDrawerActivity)

    fun inject(bookmarkActivity: BookmarkActivity)

    fun inject(mainActivity: MainActivity)

    fun inject(oAuthActivity: OAuthActivity)

    fun inject(dialogFragment: EditBookmarkDialogFragment)

    fun inject(dialogFragment: EditUserIdDialogFragment)

    fun fragmentComponent(): FragmentComponent
}
