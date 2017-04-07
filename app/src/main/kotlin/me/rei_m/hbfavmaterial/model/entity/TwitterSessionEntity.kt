package me.rei_m.hbfavmaterial.model.entity

import java.io.Serializable

/**
 * TwitterSessionのEntity.
 */
data class TwitterSessionEntity(var userId: Long = 0,
                                var userName: String = "",
                                var oAuthToken: OAuthTokenEntity = OAuthTokenEntity()) : Serializable {
    var isShare: Boolean = false
}
