package me.rei_m.hbfavmaterial.usecase.impl

import io.reactivex.Single
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEditEntity
import me.rei_m.hbfavmaterial.domain.repository.HatenaTokenRepository
import me.rei_m.hbfavmaterial.domain.service.HatenaService
import me.rei_m.hbfavmaterial.exception.HatenaUnAuthorizedException
import me.rei_m.hbfavmaterial.usecase.DisplayBookmarkEditFormUsecase

class DisplayBookmarkEditFormUsecaseImpl(private val hatenaTokenRepository: HatenaTokenRepository,
                                         private val hatenaService: HatenaService) : DisplayBookmarkEditFormUsecase {
    override fun execute(urlString: String): Single<BookmarkEditEntity> {
        return if (hatenaTokenRepository.resolve().isAuthorised) {
            hatenaService.findBookmarkByUrl(hatenaTokenRepository.resolve(), urlString)
        } else {
            Single.error(HatenaUnAuthorizedException())
        }
    }
}
