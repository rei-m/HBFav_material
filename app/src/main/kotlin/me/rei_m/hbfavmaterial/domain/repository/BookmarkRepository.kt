package me.rei_m.hbfavmaterial.domain.repository

import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.enum.ReadAfterFilter
import rx.Observable

interface BookmarkRepository {

    fun findByUserIdForFavorite(userId: String, startIndex: Int = 0): Observable<List<BookmarkEntity>>

    fun findByUserId(userId: String, readAfterFilter: ReadAfterFilter, startIndex: Int = 0): Observable<List<BookmarkEntity>>

    fun findByArticleUrl(articleUrl: String): Observable<List<BookmarkEntity>>
}
