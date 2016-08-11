package me.rei_m.hbfavmaterial.repository

import android.content.Context
import me.rei_m.hbfavmaterial.entity.UserEntity

interface UserRepository {

    fun resolve(): UserEntity

    fun store(context: Context, userEntity: UserEntity)

    fun delete(context: Context)
}
