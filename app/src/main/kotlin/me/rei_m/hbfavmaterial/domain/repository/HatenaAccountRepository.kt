package me.rei_m.hbfavmaterial.domain.repository

import rx.Observable

interface HatenaAccountRepository {
    fun contains(userId: String): Observable<Boolean>
}
