package me.rei_m.hbfavmaterial.presentation.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.model.FavoriteBookmarkModel
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.fragment.FavoriteBookmarkFragmentViewModel

@Module
class BookmarkFavoriteFragmentModule {

    @Provides
    fun provideBookmarkFavoriteViewModel(favoriteBookmarkModel: FavoriteBookmarkModel,
                                         userModel: UserModel,
                                         navigator: Navigator): FavoriteBookmarkFragmentViewModel {
        return FavoriteBookmarkFragmentViewModel(favoriteBookmarkModel,
                userModel,
                navigator)
    }
}
