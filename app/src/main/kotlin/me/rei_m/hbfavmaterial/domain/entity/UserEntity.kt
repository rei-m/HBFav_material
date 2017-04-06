package me.rei_m.hbfavmaterial.domain.entity

import java.io.Serializable

data class UserEntity(val id: String) : Serializable {

    val isCompleteSetting: Boolean
        get() = id.isNotBlank()
}
