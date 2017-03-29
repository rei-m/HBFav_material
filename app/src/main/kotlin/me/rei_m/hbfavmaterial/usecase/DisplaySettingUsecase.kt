package me.rei_m.hbfavmaterial.usecase

import io.reactivex.Single

interface DisplaySettingUsecase {
    fun execute(): Single<Triple<String, Boolean, Boolean>>
}
