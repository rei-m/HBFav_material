package me.rei_m.hbfavmaterial.viewmodel.activity.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.di.ForActivity
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.activity.BaseDrawerActivityViewModel


@Module
class BaseDrawerActivityViewModelModule {
    @Provides
    @ForActivity
    internal fun provideViewModel(userModel: UserModel,
                                  navigator: Navigator): BaseDrawerActivityViewModel =
            BaseDrawerActivityViewModel(userModel, navigator)
}
