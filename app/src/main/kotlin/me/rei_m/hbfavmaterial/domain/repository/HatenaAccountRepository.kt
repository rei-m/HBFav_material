package me.rei_m.hbfavmaterial.domain.repository

import io.reactivex.Single

interface HatenaAccountRepository {
    fun contains(userId: String): Single<Boolean>
}
