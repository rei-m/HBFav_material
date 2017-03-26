package me.rei_m.hbfavmaterial.infra.network

import io.reactivex.Completable
import io.reactivex.Single
import me.rei_m.hbfavmaterial.infra.network.response.HatenaRestApiBookmarkResponse
import retrofit2.http.*

interface HatenaOAuthApiService {

    @GET("my/bookmark")
    fun getBookmark(@Query("url") url: String): Single<HatenaRestApiBookmarkResponse>

    @FormUrlEncoded
    @POST("my/bookmark")
    fun postBookmark(@Field("url") url: String,
                     @Field("comment") comment: String,
                     @Field("comment") private: String,
                     @Field("tags") tags: Array<String>): Single<HatenaRestApiBookmarkResponse>

    @DELETE("my/bookmark")
    fun deleteBookmark(@Query("url") url: String): Completable
}
