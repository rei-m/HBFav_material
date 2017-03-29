package me.rei_m.hbfavmaterial.usecase

import io.reactivex.Single
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEditEntity

interface DisplayBookmarkEditFormUsecase {
    fun execute(urlString: String): Single<BookmarkEditEntity>
}
