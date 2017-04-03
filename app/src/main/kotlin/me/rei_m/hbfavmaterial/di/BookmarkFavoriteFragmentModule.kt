package me.rei_m.hbfavmaterial.di

import android.support.v4.app.Fragment
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.repository.BookmarkRepository
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.presentation.viewmodel.BookmarkFavoriteFragmentViewModel
import me.rei_m.hbfavmaterial.usecase.impl.GetFavoriteBookmarksUsecaseImpl

@Module
class BookmarkFavoriteFragmentModule(fragment: Fragment) {

    @Provides
    fun provideBookmarkFavoriteViewModel(bookmarkRepository: BookmarkRepository,
                                         userRepository: UserRepository,
                                         rxBus: RxBus,
                                         navigator: ActivityNavigator): BookmarkFavoriteFragmentViewModel {
        return BookmarkFavoriteFragmentViewModel(GetFavoriteBookmarksUsecaseImpl(bookmarkRepository, userRepository),
                rxBus,
                navigator)
    }
}
