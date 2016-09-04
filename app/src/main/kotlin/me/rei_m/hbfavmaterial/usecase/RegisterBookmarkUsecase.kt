package me.rei_m.hbfavmaterial.usecase

import me.rei_m.hbfavmaterial.domain.entity.BookmarkEditEntity
import rx.Observable

interface RegisterBookmarkUsecase {

    fun register(url: String,
                 title: String,
                 comment: String,
                 tags:List<String>,
                 isOpen: Boolean,
                 isCheckedReadAfter: Boolean,
                 isShareAtTwitter: Boolean): Observable<BookmarkEditEntity>
}
