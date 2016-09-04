package me.rei_m.hbfavmaterial.usecase.impl

import me.rei_m.hbfavmaterial.domain.entity.TwitterSessionEntity
import me.rei_m.hbfavmaterial.domain.repository.TwitterSessionRepository
import me.rei_m.hbfavmaterial.usecase.GetTwitterSessionUsecase

class GetTwitterSessionUsecaseImpl(private val twitterSessionRepository: TwitterSessionRepository) : GetTwitterSessionUsecase {
    override fun get(): TwitterSessionEntity {
        return twitterSessionRepository.resolve()
    }
}
