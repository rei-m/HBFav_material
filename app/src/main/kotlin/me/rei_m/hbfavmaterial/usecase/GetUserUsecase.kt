package me.rei_m.hbfavmaterial.usecase

import me.rei_m.hbfavmaterial.domain.entity.UserEntity

interface GetUserUsecase {
    fun get(): UserEntity
}
