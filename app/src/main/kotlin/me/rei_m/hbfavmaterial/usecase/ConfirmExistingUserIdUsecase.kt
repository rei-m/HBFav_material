package me.rei_m.hbfavmaterial.usecase

import io.reactivex.Single

interface ConfirmExistingUserIdUsecase {
    fun confirm(userId: String): Single<Boolean>
}
