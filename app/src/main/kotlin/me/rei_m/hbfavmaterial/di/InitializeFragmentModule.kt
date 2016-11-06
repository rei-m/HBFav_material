package me.rei_m.hbfavmaterial.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.presentation.fragment.InitializeContact
import me.rei_m.hbfavmaterial.presentation.fragment.InitializePresenter
import me.rei_m.hbfavmaterial.usecase.ConfirmExistingUserIdUsecase
import me.rei_m.hbfavmaterial.usecase.GetUserUsecase

@Module
class InitializeFragmentModule(private val context: Context) {
    @Provides
    fun provideInitializePresenter(getUserUsecase: GetUserUsecase,
                                   confirmExistingUserIdUsecase: ConfirmExistingUserIdUsecase): InitializeContact.Actions {
        return InitializePresenter(getUserUsecase, confirmExistingUserIdUsecase)
    }
}
