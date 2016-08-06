package me.rei_m.hbfavmaterial.repository

import android.content.Context
import me.rei_m.hbfavmaterial.entitiy.OAuthTokenEntity

interface HatenaTokenRepository {

    fun resolve(): OAuthTokenEntity

    fun store(context: Context, oAuthTokenEntity: OAuthTokenEntity)

    fun delete(context: Context)
}
