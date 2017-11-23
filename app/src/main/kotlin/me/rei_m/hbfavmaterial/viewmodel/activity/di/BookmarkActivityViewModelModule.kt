package me.rei_m.hbfavmaterial.viewmodel.activity.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.di.ForActivity
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.activity.BookmarkActivityViewModel

@Module
class BookmarkActivityViewModelModule {
    @Provides
    @ForActivity
    internal fun provideViewModel(hatenaService: HatenaService,
                                  navigator: Navigator): BookmarkActivityViewModel =
            BookmarkActivityViewModel(hatenaService, navigator)
}
