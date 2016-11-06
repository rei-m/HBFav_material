package me.rei_m.hbfavmaterial.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.presentation.fragment.SettingContact
import me.rei_m.hbfavmaterial.presentation.fragment.SettingPresenter
import me.rei_m.hbfavmaterial.usecase.AuthorizeTwitterUsecase
import me.rei_m.hbfavmaterial.usecase.GetHatenaTokenUsecase
import me.rei_m.hbfavmaterial.usecase.GetTwitterSessionUsecase
import me.rei_m.hbfavmaterial.usecase.GetUserUsecase

@Module
class SettingFragmentModule(private val context: Context) {
    @Provides
    fun provideSettingPresenter(getUserUsecase: GetUserUsecase,
                                getHatenaTokenUsecase: GetHatenaTokenUsecase,
                                getTwitterSessionUsecase: GetTwitterSessionUsecase,
                                authorizeTwitterUsecase: AuthorizeTwitterUsecase): SettingContact.Actions {
        return SettingPresenter(getUserUsecase, getHatenaTokenUsecase, getTwitterSessionUsecase, authorizeTwitterUsecase)
    }
}
