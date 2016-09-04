package me.rei_m.hbfavmaterial.domain.entity

import java.io.Serializable

/**
 * OAuthトークンのEntity.
 */
data class OAuthTokenEntity(var token: String = "",
                            var secretToken: String = "") : Serializable {
    val isAuthorised: Boolean
        get() = token.isNotBlank() && secretToken.isNotBlank()
}
