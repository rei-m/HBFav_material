package me.rei_m.hbfavmaterial.usecase

import me.rei_m.hbfavmaterial.domain.entity.OAuthTokenEntity

interface GetHatenaTokenUsecase {
    fun get(): OAuthTokenEntity
}
