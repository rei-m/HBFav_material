package me.rei_m.hbfavmaterial.usecase.impl

import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.domain.repository.BookmarkRepository
import me.rei_m.hbfavmaterial.usecase.GetBookmarkedUsersUsecase
import rx.Observable

class GetBookmarkedUsersUsecaseImpl(private val bookmarkRepository: BookmarkRepository) : GetBookmarkedUsersUsecase {
    override fun get(bookmarkEntity: BookmarkEntity): Observable<List<BookmarkEntity>> {
        return bookmarkRepository.findByArticleUrl(bookmarkEntity.articleEntity.url)
    }
}
