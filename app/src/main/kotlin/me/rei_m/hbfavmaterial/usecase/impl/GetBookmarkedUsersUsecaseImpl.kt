package me.rei_m.hbfavmaterial.usecase.impl

import io.reactivex.Single
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.domain.repository.BookmarkRepository
import me.rei_m.hbfavmaterial.usecase.GetBookmarkedUsersUsecase

class GetBookmarkedUsersUsecaseImpl(private val bookmarkRepository: BookmarkRepository) : GetBookmarkedUsersUsecase {
    override fun get(bookmarkEntity: BookmarkEntity): Single<List<BookmarkEntity>> {
        return bookmarkRepository.findByArticleUrl(bookmarkEntity.articleEntity.url)
    }
}
