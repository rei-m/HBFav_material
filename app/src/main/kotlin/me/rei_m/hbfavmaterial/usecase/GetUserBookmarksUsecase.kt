package me.rei_m.hbfavmaterial.usecase

import io.reactivex.Single
import me.rei_m.hbfavmaterial.constant.ReadAfterFilter
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity

interface GetUserBookmarksUsecase {

    fun get(readAfterFilter: ReadAfterFilter, startIndex: Int = 0): Single<List<BookmarkEntity>>

    fun get(userId: String, readAfterFilter: ReadAfterFilter, startIndex: Int = 0): Single<List<BookmarkEntity>>
}
