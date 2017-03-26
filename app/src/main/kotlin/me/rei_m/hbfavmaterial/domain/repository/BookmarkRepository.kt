package me.rei_m.hbfavmaterial.domain.repository

import io.reactivex.Single
import me.rei_m.hbfavmaterial.constant.ReadAfterFilter
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity

interface BookmarkRepository {

    fun findByUserIdForFavorite(userId: String, startIndex: Int = 0): Single<List<BookmarkEntity>>

    fun findByUserId(userId: String, readAfterFilter: ReadAfterFilter, startIndex: Int = 0): Single<List<BookmarkEntity>>

    fun findByArticleUrl(articleUrl: String): Single<List<BookmarkEntity>>
}
