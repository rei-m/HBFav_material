package me.rei_m.hbfavmaterial.viewmodel.widget.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.SettingFragmentViewModel

@Module
class SettingFragmentViewModelModule {
    @Provides
    @ForFragment
    internal fun provideViewModelFactory(userModel: UserModel): SettingFragmentViewModel.Factory =
            SettingFragmentViewModel.Factory(userModel)
}
