package me.rei_m.hbfavmaterial.entities

import java.io.Serializable

public data class OAuthTokenEntity(var token: String = "",
                                   var secretToken: String = "") : Serializable