package me.rei_m.hbfavmaterial.di

import android.support.v4.app.Fragment
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.model.UserModel
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.application.TwitterService
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.viewmodel.SettingFragmentViewModel

@Module
class SettingFragmentModule(fragment: Fragment) {

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
