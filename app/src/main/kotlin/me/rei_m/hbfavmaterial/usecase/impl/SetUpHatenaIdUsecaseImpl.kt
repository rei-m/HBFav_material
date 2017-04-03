package me.rei_m.hbfavmaterial.usecase.impl

import io.reactivex.Completable
import me.rei_m.hbfavmaterial.domain.entity.UserEntity
import me.rei_m.hbfavmaterial.domain.repository.HatenaAccountRepository
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.exception.HatenaUnAuthorizedException
import me.rei_m.hbfavmaterial.usecase.SetUpHatenaIdUsecase

class SetUpHatenaIdUsecaseImpl(private val hatenaAccountRepository: HatenaAccountRepository,
                               private val userRepository: UserRepository) : SetUpHatenaIdUsecase {

    override fun execute(userId: String): Completable {
        return hatenaAccountRepository.contains(userId).flatMapCompletable { hasId ->
            if (hasId) {
                userRepository.store(UserEntity(userId))
                Completable.complete()
            } else {
                Completable.error(HatenaUnAuthorizedException())
            }
        }
    }
}
