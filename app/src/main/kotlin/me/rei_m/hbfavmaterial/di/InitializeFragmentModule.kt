package me.rei_m.hbfavmaterial.di

import android.support.v4.app.Fragment
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.repository.HatenaAccountRepository
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.presentation.viewmodel.InitializeFragmentViewModel
import me.rei_m.hbfavmaterial.usecase.impl.SetUpHatenaIdUsecaseImpl
import me.rei_m.hbfavmaterial.usecase.impl.StartApplicationUsecaseImpl

@Module
class InitializeFragmentModule(fragment: Fragment) {
    @Provides
    fun provideInitializeFragmentViewModel(userRepository: UserRepository,
                                           hatenaAccountRepository: HatenaAccountRepository,
                                           rxBus: RxBus,
                                           navigator: ActivityNavigator): InitializeFragmentViewModel {
        return InitializeFragmentViewModel(StartApplicationUsecaseImpl(userRepository),
                SetUpHatenaIdUsecaseImpl(hatenaAccountRepository, userRepository),
                rxBus,
                navigator)
    }
}
