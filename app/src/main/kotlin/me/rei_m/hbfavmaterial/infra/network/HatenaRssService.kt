package me.rei_m.hbfavmaterial.infra.network

import me.rei_m.hbfavmaterial.infra.network.response.BookmarkRssXml
import me.rei_m.hbfavmaterial.infra.network.response.EntryRssXml
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

interface HatenaRssService {

    @GET("{userId}/favorite.rss")
    fun favorite(@Path("userId") userId: String, @Query("of") startIndex: Int): Observable<BookmarkRssXml>

    @GET("{userId}/rss")
    fun user(@Path("userId") userId: String, @Query("of") startIndex: Int): Observable<BookmarkRssXml>

    @GET("{userId}/rss")
    fun user(@Path("userId") userId: String, @Query("of") startIndex: Int, @Query("tag") tag: String): Observable<BookmarkRssXml>

    @GET("hatena/b/hotentry")
    fun hotentry(): Observable<EntryRssXml>

    @GET("hotentry/{entryType}")
    fun hotentry(@Path("entryType") entryType: String): Observable<EntryRssXml>

    @GET("entrylist.rss")
    fun new(): Observable<EntryRssXml>

    @GET("entrylist/{entryType}")
    fun new(@Path("entryType") entryType: String): Observable<EntryRssXml>
}
