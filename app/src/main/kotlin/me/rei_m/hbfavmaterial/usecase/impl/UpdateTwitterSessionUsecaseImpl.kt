package me.rei_m.hbfavmaterial.usecase.impl

import me.rei_m.hbfavmaterial.domain.repository.TwitterSessionRepository
import me.rei_m.hbfavmaterial.usecase.UpdateTwitterSessionUsecase

class UpdateTwitterSessionUsecaseImpl(private val twitterSessionRepository: TwitterSessionRepository) : UpdateTwitterSessionUsecase {

    override fun updateIsShare(isShare: Boolean) {
        val twitterSessionEntity = twitterSessionRepository.resolve()
        twitterSessionEntity.isShare = isShare
        twitterSessionRepository.store(twitterSessionEntity)
    }
}
