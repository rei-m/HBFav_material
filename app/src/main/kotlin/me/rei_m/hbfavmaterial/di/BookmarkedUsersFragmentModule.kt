package me.rei_m.hbfavmaterial.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.repository.BookmarkRepository
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkedUsersContact
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkedUsersPresenter
import me.rei_m.hbfavmaterial.usecase.impl.GetBookmarkedUsersUsecaseImpl

@Module
open class BookmarkedUsersFragmentModule(private val context: Context) {
    @Provides
    fun provideBookmarkedUsersPresenter(bookmarkRepository: BookmarkRepository): BookmarkedUsersContact.Actions {
        return BookmarkedUsersPresenter(GetBookmarkedUsersUsecaseImpl(bookmarkRepository))
    }
}
