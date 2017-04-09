package me.rei_m.hbfavmaterial.presentation.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.model.UserBookmarkModel
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.fragment.UserBookmarkFragmentViewModel

@Module
class BookmarkUserFragmentModule {

    @Provides
    fun provideBookmarkUserViewModel(userBookmarkModel: UserBookmarkModel,
                                     userModel: UserModel,
                                     navigator: Navigator): UserBookmarkFragmentViewModel {
        return UserBookmarkFragmentViewModel(userBookmarkModel,
                userModel,
                navigator)
    }
}
