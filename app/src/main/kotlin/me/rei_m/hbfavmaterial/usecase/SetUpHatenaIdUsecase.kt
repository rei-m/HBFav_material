package me.rei_m.hbfavmaterial.usecase

import io.reactivex.Completable

interface SetUpHatenaIdUsecase {
    fun execute(userId: String): Completable
}
