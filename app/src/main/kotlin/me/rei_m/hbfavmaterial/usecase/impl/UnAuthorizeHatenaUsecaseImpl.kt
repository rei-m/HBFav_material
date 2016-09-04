package me.rei_m.hbfavmaterial.usecase.impl

import me.rei_m.hbfavmaterial.domain.repository.HatenaTokenRepository
import me.rei_m.hbfavmaterial.usecase.UnAuthorizeHatenaUsecase

class UnAuthorizeHatenaUsecaseImpl(private val hatenaTokenRepository: HatenaTokenRepository) : UnAuthorizeHatenaUsecase {
    override fun unAuthorize() {
        hatenaTokenRepository.delete()
    }
}
