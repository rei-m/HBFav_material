package me.rei_m.hbfavmaterial.usecase.impl

import me.rei_m.hbfavmaterial.domain.entity.UserEntity
import me.rei_m.hbfavmaterial.domain.repository.HatenaAccountRepository
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.usecase.ConfirmExistingUserIdUsecase
import rx.Observable

class ConfirmExistingUserIdUsecaseImpl(private val hatenaAccountRepository: HatenaAccountRepository,
                                       private val userRepository: UserRepository) : ConfirmExistingUserIdUsecase {

    override fun confirm(userId: String): Observable<Boolean> {
        return hatenaAccountRepository.contains(userId).concatMap { isContains ->
            if (isContains) {
                userRepository.store(UserEntity(userId))
            }
            Observable.create<Boolean> {
                it.onNext(isContains)
                it.onCompleted()
            }
        }
    }
}
