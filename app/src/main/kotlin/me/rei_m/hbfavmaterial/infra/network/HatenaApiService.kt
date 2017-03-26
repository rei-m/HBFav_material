package me.rei_m.hbfavmaterial.infra.network

import io.reactivex.Single
import me.rei_m.hbfavmaterial.infra.network.response.BookmarkEntryResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HatenaApiService {

    @GET("entry/jsonlite/")
    fun entry(@Query("url") entryUrl: String): Single<BookmarkEntryResponse>

    @GET("{userId}/")
    fun userCheck(@Path("userId") userId: String): Single<String>
}
