package me.rei_m.hbfavmaterial.di

import android.support.v4.app.Fragment
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.repository.BookmarkRepository
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.presentation.viewmodel.BookmarkedUsersFragmentViewModel
import me.rei_m.hbfavmaterial.usecase.impl.GetBookmarkedUsersUsecaseImpl

@Module
open class BookmarkedUsersFragmentModule(fragment: Fragment) {
    @Provides
    fun provideBookmarkedUsersFragmentViewModel(bookmarkRepository: BookmarkRepository,
                                                rxBus: RxBus,
                                                navigator: ActivityNavigator): BookmarkedUsersFragmentViewModel {
        return BookmarkedUsersFragmentViewModel(GetBookmarkedUsersUsecaseImpl(bookmarkRepository),
                rxBus,
                navigator)
    }
}
