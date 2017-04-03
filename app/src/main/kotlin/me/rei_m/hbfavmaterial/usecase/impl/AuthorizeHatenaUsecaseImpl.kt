package me.rei_m.hbfavmaterial.usecase.impl

import io.reactivex.Completable
import io.reactivex.Single
import me.rei_m.hbfavmaterial.domain.repository.HatenaTokenRepository
import me.rei_m.hbfavmaterial.domain.service.HatenaService
import me.rei_m.hbfavmaterial.usecase.AuthorizeHatenaUsecase

class AuthorizeHatenaUsecaseImpl(private val hatenaTokenRepository: HatenaTokenRepository,
                                 private val hatenaService: HatenaService) : AuthorizeHatenaUsecase {

    override fun fetchRequestToken(): Single<String> {
        return hatenaService.fetchRequestToken()
    }

    override fun authorize(requestToken: String): Completable {
        return hatenaService.fetchAccessToken(requestToken).flatMapCompletable {
            hatenaTokenRepository.store(it)
            Completable.complete()
        }
    }
}
