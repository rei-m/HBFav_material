package me.rei_m.hbfavmaterial.usecase

import me.rei_m.hbfavmaterial.domain.entity.BookmarkEditEntity
import rx.Observable

interface GetBookmarkEditUsecase {
    fun get(urlString: String): Observable<BookmarkEditEntity>
}
