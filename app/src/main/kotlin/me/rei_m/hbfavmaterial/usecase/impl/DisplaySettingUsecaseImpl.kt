package me.rei_m.hbfavmaterial.usecase.impl

import io.reactivex.Single
import me.rei_m.hbfavmaterial.domain.repository.HatenaTokenRepository
import me.rei_m.hbfavmaterial.domain.repository.TwitterSessionRepository
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.usecase.DisplaySettingUsecase

class DisplaySettingUsecaseImpl(private val userRepository: UserRepository,
                                private val hatenaTokenRepository: HatenaTokenRepository,
                                private val twitterSessionRepository: TwitterSessionRepository) : DisplaySettingUsecase {
    override fun execute(): Single<Triple<String, Boolean, Boolean>> {
        val user = userRepository.resolve()
        return Single.just(Triple(user.id,
                hatenaTokenRepository.resolve().isAuthorised,
                twitterSessionRepository.resolve().oAuthTokenEntity.isAuthorised
        ))
    }
}