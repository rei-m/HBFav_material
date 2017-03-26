package me.rei_m.hbfavmaterial.usecase

import io.reactivex.Completable

interface DeleteBookmarkUsecase {
    fun delete(bookmarkUrl: String): Completable
}
