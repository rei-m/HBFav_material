package me.rei_m.hbfavmaterial.infra.network

import io.reactivex.Single
import me.rei_m.hbfavmaterial.infra.network.response.BookmarkRssXml
import me.rei_m.hbfavmaterial.infra.network.response.EntryRssXml
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HatenaRssService {

    @GET("{userId}/favorite.rss")
    fun favorite(@Path("userId") userId: String, @Query("of") startIndex: Int): Single<BookmarkRssXml>

    @GET("{userId}/rss")
    fun user(@Path("userId") userId: String, @Query("of") startIndex: Int): Single<BookmarkRssXml>

    @GET("{userId}/rss")
    fun user(@Path("userId") userId: String, @Query("of") startIndex: Int, @Query("tag") tag: String): Single<BookmarkRssXml>

    @GET("hotentry/{entryType}")
    fun hotentry(@Path("entryType") entryType: String): Single<EntryRssXml>

    @GET("entrylist.rss")
    fun new(): Single<EntryRssXml>

    @GET("entrylist/{entryType}")
    fun new(@Path("entryType") entryType: String): Single<EntryRssXml>
}
