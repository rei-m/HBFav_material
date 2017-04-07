package me.rei_m.hbfavmaterial.infra.network

import io.reactivex.Single
import me.rei_m.hbfavmaterial.infra.network.response.EntryRssXml
import retrofit2.http.GET

interface HatenaHotEntryRssService {
    @GET("hatena/b/hotentry")
    fun hotentry(): Single<EntryRssXml>
}
