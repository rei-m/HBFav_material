package me.rei_m.hbfavmaterial.viewmodel.activity.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.di.ForActivity
import me.rei_m.hbfavmaterial.viewmodel.activity.BookmarkActivityViewModel

@Module
class BookmarkActivityViewModelModule {
    @Provides
    @ForActivity
    internal fun provideViewModelFactory(hatenaService: HatenaService): BookmarkActivityViewModel.Factory =
            BookmarkActivityViewModel.Factory(hatenaService)
}
