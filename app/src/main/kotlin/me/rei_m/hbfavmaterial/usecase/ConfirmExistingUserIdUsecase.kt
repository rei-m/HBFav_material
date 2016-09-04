package me.rei_m.hbfavmaterial.usecase

import rx.Observable

interface ConfirmExistingUserIdUsecase {
    fun confirm(userId: String): Observable<Boolean>
}
