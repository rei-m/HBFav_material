package me.rei_m.hbfavmaterial.viewmodel.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.model.BookmarkModel
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.fragment.BookmarkedUsersFragmentViewModel

@Module
class BookmarkedUsersFragmentViewModelModule {
    @Provides
    @ForFragment
    internal fun provideViewModel(bookmarkModel: BookmarkModel,
                                  navigator: Navigator): BookmarkedUsersFragmentViewModel =
            BookmarkedUsersFragmentViewModel(bookmarkModel, navigator)
}
