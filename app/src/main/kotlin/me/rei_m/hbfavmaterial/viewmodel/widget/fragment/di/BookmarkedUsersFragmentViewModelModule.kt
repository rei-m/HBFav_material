package me.rei_m.hbfavmaterial.viewmodel.widget.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.model.BookmarkModel
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.BookmarkedUsersFragmentViewModel

@Module
class BookmarkedUsersFragmentViewModelModule(private val articleUrl: String) {
    @Provides
    @ForFragment
    internal fun provideViewModelFactory(bookmarkModel: BookmarkModel): BookmarkedUsersFragmentViewModel.Factory =
            BookmarkedUsersFragmentViewModel.Factory(bookmarkModel, articleUrl)
}
