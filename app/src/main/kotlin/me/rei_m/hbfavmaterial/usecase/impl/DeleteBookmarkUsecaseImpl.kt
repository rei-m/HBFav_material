package me.rei_m.hbfavmaterial.usecase.impl

import io.reactivex.Completable
import me.rei_m.hbfavmaterial.domain.repository.HatenaTokenRepository
import me.rei_m.hbfavmaterial.domain.service.HatenaService
import me.rei_m.hbfavmaterial.usecase.DeleteBookmarkUsecase

class DeleteBookmarkUsecaseImpl(private val hatenaTokenRepository: HatenaTokenRepository,
                                private val hatenaService: HatenaService) : DeleteBookmarkUsecase {

    override fun delete(bookmarkUrl: String): Completable {

        val oAuthTokenEntity = hatenaTokenRepository.resolve()

        return hatenaService.deleteBookmark(oAuthTokenEntity, bookmarkUrl)
    }
}
