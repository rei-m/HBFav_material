package me.rei_m.hbfavmaterial.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.repository.BookmarkRepository
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkUserContact
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkUserPresenter
import me.rei_m.hbfavmaterial.usecase.impl.GetUserBookmarksUsecaseImpl

@Module
class BookmarkUserFragmentModule(private val context: Context) {
    @Provides
    fun provideBookmarkUserPresenter(bookmarkRepository: BookmarkRepository,
                                     userRepository: UserRepository): BookmarkUserContact.Actions {
        return BookmarkUserPresenter(GetUserBookmarksUsecaseImpl(bookmarkRepository, userRepository))
    }
}
