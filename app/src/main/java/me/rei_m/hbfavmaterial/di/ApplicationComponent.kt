package me.rei_m.hbfavmaterial.di

import dagger.Component
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.activities.MainActivity
import me.rei_m.hbfavmaterial.fragments.BookmarkFavoriteFragment
import me.rei_m.hbfavmaterial.fragments.BookmarkUserFragment
import me.rei_m.hbfavmaterial.fragments.HotEntryFragment
import me.rei_m.hbfavmaterial.fragments.NewEntryFragment
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class, AppLayerModule::class))
interface ApplicationComponent {

    fun inject(application: App)

    fun inject(mainActivity: MainActivity)

    fun inject(bookmarkFavoriteFragment: BookmarkFavoriteFragment)

    fun inject(bookmarkUserFragment: BookmarkUserFragment)

    fun inject(hotEntryFragment: HotEntryFragment)

    fun inject(newEntryFragment: NewEntryFragment)
}
