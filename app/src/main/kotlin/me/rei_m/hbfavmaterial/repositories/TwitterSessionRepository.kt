package me.rei_m.hbfavmaterial.repositories

import android.content.Context
import me.rei_m.hbfavmaterial.entities.TwitterSessionEntity

interface TwitterSessionRepository {

    fun resolve(): TwitterSessionEntity

    fun store(context: Context, twitterSessionEntity: TwitterSessionEntity)

    fun delete(context: Context)
}
