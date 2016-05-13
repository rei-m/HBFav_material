package me.rei_m.hbfavmaterial.network

import me.rei_m.hbfavmaterial.network.response.HatenaRssXml
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

interface HatenaRssService {

    @GET("{userId}/favorite.rss")
    fun favorite(@Path("userId") userId: String, @Query("of") startIndex: Int): Observable<HatenaRssXml>

    @GET("{userId}/rss")
    fun user(@Path("userId") userId: String, @Query("of") startIndex: Int): Observable<HatenaRssXml>

    @GET("{userId}/rss")
    fun user(@Path("userId") userId: String, @Query("of") startIndex: Int, @Query("tag") tag: String): Observable<HatenaRssXml>
}
