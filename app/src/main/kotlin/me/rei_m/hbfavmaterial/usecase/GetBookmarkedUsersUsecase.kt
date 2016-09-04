package me.rei_m.hbfavmaterial.usecase

import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import rx.Observable

interface GetBookmarkedUsersUsecase {

    fun get(bookmarkEntity: BookmarkEntity): Observable<List<BookmarkEntity>>
}
