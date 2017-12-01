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
