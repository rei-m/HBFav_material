package me.rei_m.hbfavmaterial.viewmodel.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.model.UserBookmarkModel
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.fragment.UserBookmarkFragmentViewModel

@Module
class UserBookmarkFragmentViewModelModule {
    @Provides
    @ForFragment
    internal fun provideViewModel(userBookmarkModel: UserBookmarkModel,
                                  userModel: UserModel,
                                  navigator: Navigator): UserBookmarkFragmentViewModel =
            UserBookmarkFragmentViewModel(userBookmarkModel, userModel, navigator)
}
