package me.rei_m.hbfavmaterial.di

import android.support.v4.app.DialogFragment
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.repository.HatenaAccountRepository
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.viewmodel.EditUserIdDialogFragmentViewModel
import me.rei_m.hbfavmaterial.usecase.impl.DisplayEditUserIdDialogUsecaseImpl
import me.rei_m.hbfavmaterial.usecase.impl.SetUpHatenaIdUsecaseImpl

@Module
class EditUserIdDialogFragmentModule(fragment: DialogFragment) {

    @Provides
    fun provideEditUserIdDialogFragmentViewModel(userRepository: UserRepository,
                                                 hatenaAccountRepository: HatenaAccountRepository,
                                                 rxBus: RxBus): EditUserIdDialogFragmentViewModel {
        return EditUserIdDialogFragmentViewModel(DisplayEditUserIdDialogUsecaseImpl(userRepository),
                SetUpHatenaIdUsecaseImpl(hatenaAccountRepository, userRepository),
                rxBus)
    }
}
