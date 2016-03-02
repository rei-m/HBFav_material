package me.rei_m.hbfavmaterial.entities

import java.io.Serializable

/**
 * OAuthトークンのEntity.
 */
data class OAuthTokenEntity(var token: String = "",
                            var secretToken: String = "") : Serializable
