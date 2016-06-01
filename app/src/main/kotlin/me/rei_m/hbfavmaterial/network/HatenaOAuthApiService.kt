package me.rei_m.hbfavmaterial.network

import me.rei_m.hbfavmaterial.network.response.HatenaRestApiBookmarkResponse
import retrofit2.http.*
import rx.Observable

interface HatenaOAuthApiService {

    @GET("my/bookmark")
    fun getBookmark(@Query("url") url: String): Observable<HatenaRestApiBookmarkResponse>

    @FormUrlEncoded
    @POST("my/bookmark")
    fun postBookmark(@Field("url") url: String,
                     @Field("comment") comment: String,
                     @Field("comment") private: String,
                     @Field("tags") tags: Array<String>): Observable<HatenaRestApiBookmarkResponse>

    @DELETE("my/bookmark")
    fun deleteBookmark(@Query("url") url: String): Observable<Void?>
}
