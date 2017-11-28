package me.rei_m.hbfavmaterial.viewmodel.widget.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.model.FavoriteBookmarkModel
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.FavoriteBookmarkFragmentViewModel

@Module
class FavoriteBookmarkFragmentViewModelModule {
    @Provides
    @ForFragment
    internal fun provideViewModelFactory(favoriteBookmarkModel: FavoriteBookmarkModel,
                                         userModel: UserModel): FavoriteBookmarkFragmentViewModel.Factory =
            FavoriteBookmarkFragmentViewModel.Factory(favoriteBookmarkModel, userModel)
}
