package me.rei_m.hbfavmaterial.entities

import java.io.Serializable

/**
 * ユーザー情報のEntity.
 */
data class UserEntity(val id: String) : Serializable {

    val isCompleteSetting: Boolean
        get() = id.isNotBlank()

    fun isSameId(target: String): Boolean = id.equals(target)
}
