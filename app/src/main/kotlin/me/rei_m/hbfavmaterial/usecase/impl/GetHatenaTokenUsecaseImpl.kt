package me.rei_m.hbfavmaterial.usecase.impl

import me.rei_m.hbfavmaterial.domain.entity.OAuthTokenEntity
import me.rei_m.hbfavmaterial.domain.repository.HatenaTokenRepository
import me.rei_m.hbfavmaterial.usecase.GetHatenaTokenUsecase

class GetHatenaTokenUsecaseImpl(private val hatenaTokenRepository: HatenaTokenRepository) : GetHatenaTokenUsecase {
    override fun get(): OAuthTokenEntity {
        return hatenaTokenRepository.resolve()
    }
}
