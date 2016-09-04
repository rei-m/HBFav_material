package me.rei_m.hbfavmaterial.usecase

import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import rx.Observable

interface GetFavoriteBookmarksUsecase {

    fun get(startIndex: Int = 0): Observable<List<BookmarkEntity>>
}