package me.rei_m.hbfavmaterial.usecase

import io.reactivex.Single
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEditEntity

interface GetBookmarkEditUsecase {
    fun get(urlString: String): Single<BookmarkEditEntity>
}
