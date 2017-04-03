package me.rei_m.hbfavmaterial.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.repository.HatenaTokenRepository
import me.rei_m.hbfavmaterial.domain.service.HatenaService
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.presentation.viewmodel.BookmarkActivityViewModel
import me.rei_m.hbfavmaterial.usecase.impl.DisplayBookmarkEditFormUsecaseImpl

@Module
class BookmarkActivityModule() {
    @Provides
    fun provideBookmarkActivityViewModel(hatenaTokenRepository: HatenaTokenRepository,
                                         hatenaService: HatenaService,
                                         rxBus: RxBus,
                                         navigator: ActivityNavigator): BookmarkActivityViewModel {
        return BookmarkActivityViewModel(DisplayBookmarkEditFormUsecaseImpl(hatenaTokenRepository, hatenaService),
                rxBus,
                navigator)
    }
}
