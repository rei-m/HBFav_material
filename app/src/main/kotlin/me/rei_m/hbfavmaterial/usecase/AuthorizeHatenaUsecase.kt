package me.rei_m.hbfavmaterial.usecase

import rx.Observable

interface AuthorizeHatenaUsecase {

    fun fetchRequestToken(): Observable<String>

    fun authorize(requestToken: String): Observable<Unit>
}
