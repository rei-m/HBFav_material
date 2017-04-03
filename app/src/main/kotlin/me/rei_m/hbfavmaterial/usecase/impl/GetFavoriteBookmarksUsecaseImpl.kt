package me.rei_m.hbfavmaterial.usecase.impl

import io.reactivex.Single
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.domain.repository.BookmarkRepository
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.usecase.GetFavoriteBookmarksUsecase

class GetFavoriteBookmarksUsecaseImpl(private val bookmarkRepository: BookmarkRepository,
                                      private val userRepository: UserRepository) : GetFavoriteBookmarksUsecase {

    override fun get(startIndex: Int): Single<List<BookmarkEntity>> {
        return bookmarkRepository.findByUserIdForFavorite(userRepository.resolve().id, startIndex)
    }
}
