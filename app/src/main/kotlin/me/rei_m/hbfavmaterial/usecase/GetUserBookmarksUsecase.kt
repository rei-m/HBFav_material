package me.rei_m.hbfavmaterial.usecase

import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.constant.ReadAfterFilter
import rx.Observable

interface GetUserBookmarksUsecase {

    fun get(readAfterFilter: ReadAfterFilter, startIndex: Int = 0): Observable<List<BookmarkEntity>>

    fun get(userId: String, readAfterFilter: ReadAfterFilter, startIndex: Int = 0): Observable<List<BookmarkEntity>>
}
