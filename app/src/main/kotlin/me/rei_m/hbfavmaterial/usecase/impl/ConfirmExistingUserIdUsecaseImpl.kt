package me.rei_m.hbfavmaterial.usecase.impl

import io.reactivex.Single
import me.rei_m.hbfavmaterial.domain.entity.UserEntity
import me.rei_m.hbfavmaterial.domain.repository.HatenaAccountRepository
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.usecase.ConfirmExistingUserIdUsecase

class ConfirmExistingUserIdUsecaseImpl(private val hatenaAccountRepository: HatenaAccountRepository,
                                       private val userRepository: UserRepository) : ConfirmExistingUserIdUsecase {

    override fun confirm(userId: String): Single<Boolean> {
        return hatenaAccountRepository.contains(userId).flatMap { isContains ->
            if (isContains) {
                userRepository.store(UserEntity(userId))
            }
            Single.just(isContains)
        }
    }
}
