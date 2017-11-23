package me.rei_m.hbfavmaterial.viewmodel.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.model.FavoriteBookmarkModel
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.fragment.FavoriteBookmarkFragmentViewModel

@Module
class FavoriteBookmarkFragmentViewModelModule {
    @Provides
    @ForFragment
    internal fun provideViewModel(favoriteBookmarkModel: FavoriteBookmarkModel,
                                  userModel: UserModel,
                                  navigator: Navigator): FavoriteBookmarkFragmentViewModel =
            FavoriteBookmarkFragmentViewModel(favoriteBookmarkModel, userModel, navigator)
}
