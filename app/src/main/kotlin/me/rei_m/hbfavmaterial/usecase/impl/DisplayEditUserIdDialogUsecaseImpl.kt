package me.rei_m.hbfavmaterial.usecase.impl

import io.reactivex.Single
import me.rei_m.hbfavmaterial.domain.repository.UserRepository

class DisplayEditUserIdDialogUsecaseImpl(private val userRepository: UserRepository) : DisplayEditUserIdDialogUsecase {
    override fun execute(): Single<String> {
        return Single.just(userRepository.resolve().id)
    }
}
