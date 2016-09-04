package me.rei_m.hbfavmaterial.usecase.impl

import me.rei_m.hbfavmaterial.domain.repository.HatenaTokenRepository
import me.rei_m.hbfavmaterial.domain.service.HatenaService
import me.rei_m.hbfavmaterial.usecase.AuthorizeHatenaUsecase
import rx.Observable

class AuthorizeHatenaUsecaseImpl(private val hatenaTokenRepository: HatenaTokenRepository,
                                 private val hatenaService: HatenaService) : AuthorizeHatenaUsecase {

    override fun fetchRequestToken(): Observable<String> {
        return hatenaService.fetchRequestToken()
    }

    override fun authorize(requestToken: String): Observable<Unit> {
        return hatenaService.fetchAccessToken(requestToken).concatMap {
            hatenaTokenRepository.store(it)
            Observable.create<Unit> {
                it.onNext(Unit)
                it.onCompleted()
            }
        }
    }
}
