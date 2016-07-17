package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.activities.BaseDrawerActivity
import me.rei_m.hbfavmaterial.activities.BookmarkActivity
import me.rei_m.hbfavmaterial.activities.MainActivity
import me.rei_m.hbfavmaterial.activities.OAuthActivity

@Subcomponent(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {

    fun inject(baseActivityWithDrawer: BaseDrawerActivity)
    
    fun inject(bookmarkActivity: BookmarkActivity)

    fun inject(mainActivity: MainActivity)

    fun inject(oAuthActivity: OAuthActivity)

    fun plus(module: FragmentModule): FragmentComponent
}
