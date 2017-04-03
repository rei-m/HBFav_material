package me.rei_m.hbfavmaterial.di

import android.support.v4.app.Fragment
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.repository.BookmarkRepository
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.presentation.viewmodel.BookmarkUserFragmentViewModel
import me.rei_m.hbfavmaterial.usecase.impl.GetUserBookmarksUsecaseImpl

@Module
class BookmarkUserFragmentModule(fragment: Fragment) {

    @Provides
    fun provideBookmarkUserViewModel(bookmarkRepository: BookmarkRepository,
                                     userRepository: UserRepository,
                                     rxBus: RxBus,
                                     navigator: ActivityNavigator): BookmarkUserFragmentViewModel {
        return BookmarkUserFragmentViewModel(GetUserBookmarksUsecaseImpl(bookmarkRepository, userRepository),
                rxBus,
                navigator)
    }
}
