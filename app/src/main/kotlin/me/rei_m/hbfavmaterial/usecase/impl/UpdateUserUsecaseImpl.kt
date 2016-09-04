package me.rei_m.hbfavmaterial.usecase.impl

import me.rei_m.hbfavmaterial.domain.entity.UserEntity
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.usecase.UpdateUserUsecase

class UpdateUserUsecaseImpl(private val userRepository: UserRepository) : UpdateUserUsecase {

    override fun updateUserId(userId: String) {
        userRepository.store(UserEntity(userId))
    }

    override fun updateIsCheckedPostBookmarkOpen(isChecked: Boolean) {
        val userEntity = userRepository.resolve()
        userEntity.isCheckedPostBookmarkOpen = isChecked
        userRepository.store(userEntity)
    }

    override fun updateIsCheckedPostBookmarkReadAfter(isChecked: Boolean) {
        val userEntity = userRepository.resolve()
        userEntity.isCheckedPostBookmarkReadAfter = isChecked
        userRepository.store(userEntity)
    }
}
