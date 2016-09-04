package me.rei_m.hbfavmaterial.domain.repository

import me.rei_m.hbfavmaterial.domain.entity.OAuthTokenEntity

interface HatenaTokenRepository {

    fun resolve(): OAuthTokenEntity

    fun store(oAuthTokenEntity: OAuthTokenEntity)

    fun delete()
}
