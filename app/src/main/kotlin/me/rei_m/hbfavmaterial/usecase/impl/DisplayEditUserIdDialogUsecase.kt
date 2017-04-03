package me.rei_m.hbfavmaterial.usecase.impl

import io.reactivex.Single

interface DisplayEditUserIdDialogUsecase {
    fun execute(): Single<String>
}
