package me.rei_m.hbfavmaterial.viewmodel.widget.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.model.UserBookmarkModel
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.UserBookmarkFragmentViewModel

@Module
class UserBookmarkFragmentViewModelModule {
    @Provides
    @ForFragment
    internal fun provideViewModelFactory(userBookmarkModel: UserBookmarkModel,
                                         userModel: UserModel): UserBookmarkFragmentViewModel.Factory {
        return UserBookmarkFragmentViewModel.Factory(userBookmarkModel, userModel)
    }
}
