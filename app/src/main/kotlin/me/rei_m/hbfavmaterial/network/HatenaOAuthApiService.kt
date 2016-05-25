package me.rei_m.hbfavmaterial.network

import me.rei_m.hbfavmaterial.network.response.HatenaRestApiBookmarkResponse
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

interface HatenaOAuthApiService {

    @GET("my/bookmark")
    fun getBookmark(@Query("url") entryUrl: String): Observable<HatenaRestApiBookmarkResponse>
}
