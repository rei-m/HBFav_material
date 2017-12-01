/*
 * Copyright (c) 2017. Rei Matsushita
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

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
