package me.rei_m.hbfavmaterial.domain.entity

import java.io.Serializable

/**
 * TwitterSessionのEntity.
 */
data class TwitterSessionEntity(var userId: Long = 0,
                                var userName: String = "",
                                var oAuthToken: OAuthTokenEntity = OAuthTokenEntity()) : Serializable {
    var isShare: Boolean = false
}
