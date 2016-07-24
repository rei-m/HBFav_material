package me.rei_m.hbfavmaterial.repositories

import android.content.Context
import me.rei_m.hbfavmaterial.entities.OAuthTokenEntity

interface HatenaTokenRepository {

    fun resolve(): OAuthTokenEntity

    fun store(context: Context, oAuthTokenEntity: OAuthTokenEntity)

    fun delete(context: Context)
}
