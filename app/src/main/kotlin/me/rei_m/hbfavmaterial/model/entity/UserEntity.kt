package me.rei_m.hbfavmaterial.model.entity

import java.io.Serializable

data class UserEntity(val id: String) : Serializable {

    val isCompleteSetting: Boolean
        get() = id.isNotBlank()
}
