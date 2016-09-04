package me.rei_m.hbfavmaterial.usecase

import me.rei_m.hbfavmaterial.domain.entity.TwitterSessionEntity

interface GetTwitterSessionUsecase {
    fun get(): TwitterSessionEntity
}
