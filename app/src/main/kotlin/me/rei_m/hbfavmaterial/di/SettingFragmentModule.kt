package me.rei_m.hbfavmaterial.di

import android.support.v4.app.Fragment
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.repository.HatenaTokenRepository
import me.rei_m.hbfavmaterial.domain.repository.TwitterSessionRepository
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.presentation.viewmodel.SettingFragmentViewModel
import me.rei_m.hbfavmaterial.usecase.impl.DisplaySettingUsecaseImpl

@Module
class SettingFragmentModule(fragment: Fragment) {

    @Provides
    fun provideSettingFragmentViewModel(userRepository: UserRepository,
                                        hatenaTokenRepository: HatenaTokenRepository,
                                        twitterSessionRepository: TwitterSessionRepository,
                                        rxBus: RxBus,
                                        navigator: ActivityNavigator): SettingFragmentViewModel {
        return SettingFragmentViewModel(DisplaySettingUsecaseImpl(userRepository,
                hatenaTokenRepository,
                twitterSessionRepository),
                rxBus,
                navigator)
    }
}
