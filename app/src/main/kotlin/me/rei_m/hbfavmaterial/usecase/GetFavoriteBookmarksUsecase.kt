package me.rei_m.hbfavmaterial.usecase

import io.reactivex.Single
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity

interface GetFavoriteBookmarksUsecase {

    fun get(startIndex: Int = 0): Single<List<BookmarkEntity>>
}
