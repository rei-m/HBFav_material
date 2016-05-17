package me.rei_m.hbfavmaterial.network

import me.rei_m.hbfavmaterial.network.response.BookmarkEntryResponse
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

interface HatenaApiService {

    @GET("entry/jsonlite/")
    fun entry(@Query("url") entryUrl: String): Observable<BookmarkEntryResponse>
}
