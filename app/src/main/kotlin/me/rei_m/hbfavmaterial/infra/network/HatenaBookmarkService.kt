package me.rei_m.hbfavmaterial.infra.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface HatenaBookmarkService {
    @GET("{userId}/")
    fun userCheck(@Path("userId") userId: String): Single<String>
}
