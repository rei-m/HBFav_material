package me.rei_m.hbfavmaterial.usecase

import io.reactivex.Single
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity

interface GetBookmarkedUsersUsecase {

    fun get(bookmarkEntity: BookmarkEntity): Single<List<BookmarkEntity>>
}
