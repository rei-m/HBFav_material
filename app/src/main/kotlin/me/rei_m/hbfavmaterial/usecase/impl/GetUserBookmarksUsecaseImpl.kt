package me.rei_m.hbfavmaterial.usecase.impl

import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.domain.repository.BookmarkRepository
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.enum.ReadAfterFilter
import me.rei_m.hbfavmaterial.usecase.GetUserBookmarksUsecase
import rx.Observable

class GetUserBookmarksUsecaseImpl(private val bookmarkRepository: BookmarkRepository,
                                  private val userRepository: UserRepository) : GetUserBookmarksUsecase {

    override fun get(readAfterFilter: ReadAfterFilter, startIndex: Int): Observable<List<BookmarkEntity>> {
        return bookmarkRepository.findByUserId(userRepository.resolve().id, readAfterFilter, startIndex)
    }

    override fun get(userId: String, readAfterFilter: ReadAfterFilter, startIndex: Int): Observable<List<BookmarkEntity>> {
        return bookmarkRepository.findByUserId(userId, readAfterFilter, startIndex)
    }
}
