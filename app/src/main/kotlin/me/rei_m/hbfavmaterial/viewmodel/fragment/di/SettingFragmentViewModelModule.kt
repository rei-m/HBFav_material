package me.rei_m.hbfavmaterial.viewmodel.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.application.TwitterService
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.fragment.SettingFragmentViewModel

@Module
class SettingFragmentViewModelModule {
    @Provides
    @ForFragment
    internal fun provideViewModel(userModel: UserModel,
                                  hatenaService: HatenaService,
                                  twitterService: TwitterService,
                                  navigator: Navigator): SettingFragmentViewModel =
            SettingFragmentViewModel(userModel, hatenaService, twitterService, navigator)
}
