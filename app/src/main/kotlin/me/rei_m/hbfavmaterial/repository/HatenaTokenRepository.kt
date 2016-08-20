package me.rei_m.hbfavmaterial.repository

import me.rei_m.hbfavmaterial.entity.OAuthTokenEntity

interface HatenaTokenRepository {

    fun resolve(): OAuthTokenEntity

    fun store(oAuthTokenEntity: OAuthTokenEntity)

    fun delete()
}
