package me.rei_m.hbfavmaterial.repository

import me.rei_m.hbfavmaterial.entity.UserEntity

interface UserRepository {

    fun resolve(): UserEntity

    fun store(userEntity: UserEntity)

    fun delete()
}
