package me.rei_m.hbfavmaterial.di

import android.support.v4.app.Fragment
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.repository.BookmarkRepository
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkFavoriteContact
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkFavoritePresenter
import me.rei_m.hbfavmaterial.presentation.viewmodel.BookmarkFavoriteFragmentViewModel
import me.rei_m.hbfavmaterial.usecase.impl.GetFavoriteBookmarksUsecaseImpl

@Module
class BookmarkFavoriteFragmentModule(private val fragment: Fragment) {

    @Provides
    fun provideBookmarkFavoriteViewModel(bookmarkRepository: BookmarkRepository,
                                         userRepository: UserRepository): BookmarkFavoriteFragmentViewModel {
        return BookmarkFavoriteFragmentViewModel(GetFavoriteBookmarksUsecaseImpl(bookmarkRepository, userRepository))
    }

    @Provides
    fun provideBookmarkFavoritePresenter(bookmarkRepository: BookmarkRepository,
                                         userRepository: UserRepository): BookmarkFavoriteContact.Actions {
        return BookmarkFavoritePresenter(GetFavoriteBookmarksUsecaseImpl(bookmarkRepository, userRepository))
    }
}
