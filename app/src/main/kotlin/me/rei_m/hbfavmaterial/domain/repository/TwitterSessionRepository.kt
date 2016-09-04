package me.rei_m.hbfavmaterial.domain.repository

import me.rei_m.hbfavmaterial.domain.entity.TwitterSessionEntity

interface TwitterSessionRepository {

    fun resolve(): TwitterSessionEntity

    fun store(twitterSessionEntity: TwitterSessionEntity)

    fun delete()
}
