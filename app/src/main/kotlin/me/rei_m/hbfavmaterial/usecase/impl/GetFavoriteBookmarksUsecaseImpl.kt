package me.rei_m.hbfavmaterial.usecase.impl

import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.domain.repository.BookmarkRepository
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.usecase.GetFavoriteBookmarksUsecase
import rx.Observable

class GetFavoriteBookmarksUsecaseImpl(private val bookmarkRepository: BookmarkRepository,
                                      private val userRepository: UserRepository) : GetFavoriteBookmarksUsecase {

    override fun get(startIndex: Int): Observable<List<BookmarkEntity>> {
        return bookmarkRepository.findByUserIdForFavorite(userRepository.resolve().id, startIndex)
    }
}
