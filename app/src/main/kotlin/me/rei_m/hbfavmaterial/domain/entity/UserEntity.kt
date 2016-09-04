package me.rei_m.hbfavmaterial.domain.entity

import java.io.Serializable

/**
 * ユーザー情報のEntity.
 */
data class UserEntity(val id: String,
                      var isCheckedPostBookmarkOpen: Boolean = true,
                      var isCheckedPostBookmarkReadAfter: Boolean = false) : Serializable {

    val isCompleteSetting: Boolean
        get() = id.isNotBlank()

    fun isSameId(target: String): Boolean = id.equals(target)
}
