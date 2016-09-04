package me.rei_m.hbfavmaterial.usecase

import rx.Observable

interface DeleteBookmarkUsecase {
    fun delete(bookmarkUrl: String): Observable<Void?>
}
