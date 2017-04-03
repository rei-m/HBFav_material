package me.rei_m.hbfavmaterial.usecase

import io.reactivex.Single

interface StartApplicationUsecase {
    fun execute(): Single<Boolean>
}
