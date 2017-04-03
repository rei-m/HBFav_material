package me.rei_m.hbfavmaterial.usecase.impl

import io.reactivex.Single
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEditEntity
import me.rei_m.hbfavmaterial.domain.repository.HatenaTokenRepository
import me.rei_m.hbfavmaterial.domain.service.HatenaService
import me.rei_m.hbfavmaterial.usecase.GetBookmarkEditUsecase

class GetBookmarkEditUsecaseImpl(private val hatenaTokenRepository: HatenaTokenRepository,
                                 private val hatenaService: HatenaService) : GetBookmarkEditUsecase {
    override fun get(urlString: String): Single<BookmarkEditEntity> {
        return hatenaService.findBookmarkByUrl(hatenaTokenRepository.resolve(), urlString)
    }
}
