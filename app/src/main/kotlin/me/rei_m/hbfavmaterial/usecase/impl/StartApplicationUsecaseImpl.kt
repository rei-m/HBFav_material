package me.rei_m.hbfavmaterial.usecase.impl

import io.reactivex.Single
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.usecase.StartApplicationUsecase

class StartApplicationUsecaseImpl(private val userRepository: UserRepository) : StartApplicationUsecase {
    override fun execute(): Single<Boolean> {
        return Single.just(userRepository.resolve().isCompleteSetting)
    }
}
