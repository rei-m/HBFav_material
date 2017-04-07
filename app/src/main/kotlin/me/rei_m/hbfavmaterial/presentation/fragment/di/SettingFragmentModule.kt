package me.rei_m.hbfavmaterial.presentation.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.application.TwitterService
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.fragment.SettingFragmentViewModel

@Module
class SettingFragmentModule {

    @Provides
    fun provideSettingFragmentViewModel(userModel: UserModel,
                                        hatenaService: HatenaService,
                                        twitterService: TwitterService,
                                        rxBus: RxBus,
                                        navigator: Navigator): SettingFragmentViewModel {
        return SettingFragmentViewModel(userModel,
                hatenaService,
                twitterService,
                rxBus,
                navigator)
    }
}
