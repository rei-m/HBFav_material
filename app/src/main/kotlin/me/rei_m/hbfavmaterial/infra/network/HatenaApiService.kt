package me.rei_m.hbfavmaterial.infra.network

import me.rei_m.hbfavmaterial.infra.network.response.BookmarkEntryResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

interface HatenaApiService {

    @GET("entry/jsonlite/")
    fun entry(@Query("url") entryUrl: String): Observable<BookmarkEntryResponse>

    @GET("{userId}/")
    fun userCheck(@Path("userId") userId: String): Observable<String>
}
