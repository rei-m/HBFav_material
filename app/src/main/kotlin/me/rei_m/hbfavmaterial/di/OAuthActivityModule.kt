package me.rei_m.hbfavmaterial.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.repository.HatenaTokenRepository
import me.rei_m.hbfavmaterial.domain.service.HatenaService
import me.rei_m.hbfavmaterial.usecase.AuthorizeHatenaUsecase
import me.rei_m.hbfavmaterial.usecase.UnAuthorizeHatenaUsecase
import me.rei_m.hbfavmaterial.usecase.impl.AuthorizeHatenaUsecaseImpl
import me.rei_m.hbfavmaterial.usecase.impl.UnAuthorizeHatenaUsecaseImpl


@Module
class OAuthActivityModule(private val context: Context) {

    @Provides
    fun provideAuthorizeHatenaUsecase(hatenaTokenRepository: HatenaTokenRepository,
                                      hatenaService: HatenaService): AuthorizeHatenaUsecase {
        return AuthorizeHatenaUsecaseImpl(hatenaTokenRepository, hatenaService)
    }

    @Provides
    fun provideUnAuthorizeHatenaUsecase(hatenaTokenRepository: HatenaTokenRepository): UnAuthorizeHatenaUsecase {
        return UnAuthorizeHatenaUsecaseImpl(hatenaTokenRepository)
    }
}
