package me.rei_m.hbfavmaterial.presentation.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.model.BookmarkModel
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.fragment.BookmarkedUsersFragmentViewModel

@Module
open class BookmarkedUsersFragmentModule {
    @Provides
    fun provideBookmarkedUsersFragmentViewModel(bookmarkModel: BookmarkModel,
                                                rxBus: RxBus,
                                                navigator: Navigator): BookmarkedUsersFragmentViewModel {
        return BookmarkedUsersFragmentViewModel(bookmarkModel,
                rxBus,
                navigator)
    }
}
