package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.activity.BaseDrawerActivity
import me.rei_m.hbfavmaterial.presentation.activity.BookmarkActivity
import me.rei_m.hbfavmaterial.presentation.activity.MainActivity
import me.rei_m.hbfavmaterial.presentation.activity.OAuthActivity
import me.rei_m.hbfavmaterial.presentation.fragment.EditBookmarkDialogFragment
import me.rei_m.hbfavmaterial.presentation.fragment.EditUserIdDialogFragment
import me.rei_m.hbfavmaterial.presentation.fragment.EditUserIdDialogPresenter

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
