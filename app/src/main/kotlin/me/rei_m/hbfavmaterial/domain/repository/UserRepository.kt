package me.rei_m.hbfavmaterial.domain.repository

import me.rei_m.hbfavmaterial.domain.entity.UserEntity

interface UserRepository {

    fun resolve(): UserEntity

    fun store(userEntity: UserEntity)

    fun delete()
}
