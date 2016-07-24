package me.rei_m.hbfavmaterial.repositories

import android.content.Context
import me.rei_m.hbfavmaterial.entities.UserEntity

interface UserRepository {

    fun resolve(): UserEntity

    fun store(context: Context, userEntity: UserEntity)

    fun delete(context: Context)
}
