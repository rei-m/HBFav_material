package me.rei_m.hbfavmaterial.presentation.activity.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.activity.BookmarkActivityViewModel

@Module
class BookmarkActivityModule {
    @Provides
    fun provideBookmarkActivityViewModel(hatenaService: HatenaService,
                                         rxBus: RxBus,
                                         navigator: Navigator): BookmarkActivityViewModel {
        return BookmarkActivityViewModel(hatenaService,
                rxBus,
                navigator)
    }
}
