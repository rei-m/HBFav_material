package me.rei_m.hbfavmaterial.service

import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.enums.ReadAfterFilter
import rx.Observable

interface BookmarkService {
    fun findByUserIdForFavorite(userId: String, startIndex: Int = 0): Observable<List<BookmarkEntity>>

    fun findByUserId(userId: String, readAfterFilter: ReadAfterFilter, startIndex: Int = 0): Observable<List<BookmarkEntity>>

    fun findByArticleUrl(articleUrl: String): Observable<List<BookmarkEntity>>
}
