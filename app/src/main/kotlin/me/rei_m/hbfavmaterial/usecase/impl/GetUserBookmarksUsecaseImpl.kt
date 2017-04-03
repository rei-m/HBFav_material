package me.rei_m.hbfavmaterial.usecase.impl

import io.reactivex.Single
import me.rei_m.hbfavmaterial.constant.ReadAfterFilter
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.domain.repository.BookmarkRepository
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.usecase.GetUserBookmarksUsecase

class GetUserBookmarksUsecaseImpl(private val bookmarkRepository: BookmarkRepository,
                                  private val userRepository: UserRepository) : GetUserBookmarksUsecase {

    override fun get(readAfterFilter: ReadAfterFilter, startIndex: Int): Single<List<BookmarkEntity>> {
        return bookmarkRepository.findByUserId(userRepository.resolve().id, readAfterFilter, startIndex)
    }

    override fun get(userId: String, readAfterFilter: ReadAfterFilter, startIndex: Int): Single<List<BookmarkEntity>> {
        return bookmarkRepository.findByUserId(userId, readAfterFilter, startIndex)
    }
}
