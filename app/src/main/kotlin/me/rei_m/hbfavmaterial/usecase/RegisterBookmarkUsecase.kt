package me.rei_m.hbfavmaterial.usecase

import io.reactivex.Single
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEditEntity

interface RegisterBookmarkUsecase {

    fun register(url: String,
                 title: String,
                 comment: String,
                 tags: List<String>,
                 isOpen: Boolean,
                 isCheckedReadAfter: Boolean,
                 isShareAtTwitter: Boolean): Single<BookmarkEditEntity>
}
