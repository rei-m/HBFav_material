package me.rei_m.hbfavmaterial.presentation.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.fragment.BookmarkFragmentViewModel

@Module
class BookmarkFragmentModule {

    @Provides
    fun provideBookmarkViewModel(rxBus: RxBus,
                                 navigator: Navigator): BookmarkFragmentViewModel {
        return BookmarkFragmentViewModel(rxBus, navigator)
    }
}
