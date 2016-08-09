package me.rei_m.hbfavmaterial.repository

import android.content.Context
import me.rei_m.hbfavmaterial.entity.TwitterSessionEntity

interface TwitterSessionRepository {

    fun resolve(): TwitterSessionEntity

    fun store(context: Context, twitterSessionEntity: TwitterSessionEntity)

    fun delete(context: Context)
}
