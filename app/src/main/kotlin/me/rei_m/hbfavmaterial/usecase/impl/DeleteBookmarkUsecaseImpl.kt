package me.rei_m.hbfavmaterial.usecase.impl

import me.rei_m.hbfavmaterial.domain.repository.HatenaTokenRepository
import me.rei_m.hbfavmaterial.usecase.DeleteBookmarkUsecase
import me.rei_m.hbfavmaterial.domain.service.HatenaService
import rx.Observable

class DeleteBookmarkUsecaseImpl(private val hatenaTokenRepository: HatenaTokenRepository,
                                private val hatenaService: HatenaService) : DeleteBookmarkUsecase {

    override fun delete(bookmarkUrl: String): Observable<Void?> {

        val oAuthTokenEntity = hatenaTokenRepository.resolve()

        return hatenaService.deleteBookmark(oAuthTokenEntity, bookmarkUrl)
    }
}
