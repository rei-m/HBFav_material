package me.rei_m.hbfavmaterial.usecase.impl

import me.rei_m.hbfavmaterial.domain.entity.UserEntity
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.usecase.GetUserUsecase

class GetUserUsecaseImpl(private val userRepository: UserRepository) : GetUserUsecase {
    override fun get(): UserEntity {
        return userRepository.resolve()
    }
}
