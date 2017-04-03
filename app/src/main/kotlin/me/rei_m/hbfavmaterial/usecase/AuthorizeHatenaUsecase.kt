package me.rei_m.hbfavmaterial.usecase

import io.reactivex.Completable
import io.reactivex.Single

interface AuthorizeHatenaUsecase {

    fun fetchRequestToken(): Single<String>

    fun authorize(requestToken: String): Completable
}
