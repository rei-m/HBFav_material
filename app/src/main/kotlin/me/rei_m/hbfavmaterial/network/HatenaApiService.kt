package me.rei_m.hbfavmaterial.network

import me.rei_m.hbfavmaterial.network.response.BookmarkEntryResponse
import retrofit2.http.GET
import retrofit2.http.Path
import rx.Observable

interface HatenaApiService {
    @GET("entry/jsonlite/{entryUrl}")
    fun entry(@Path("entryUrl") entryUrl: String): Observable<BookmarkEntryResponse>
}
