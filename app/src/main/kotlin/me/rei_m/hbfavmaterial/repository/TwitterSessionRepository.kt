package me.rei_m.hbfavmaterial.repository

import me.rei_m.hbfavmaterial.entity.TwitterSessionEntity

interface TwitterSessionRepository {

    fun resolve(): TwitterSessionEntity

    fun store(twitterSessionEntity: TwitterSessionEntity)

    fun delete()
}
