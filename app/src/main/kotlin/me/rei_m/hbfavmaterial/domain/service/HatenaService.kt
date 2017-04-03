package me.rei_m.hbfavmaterial.domain.service

import io.reactivex.Completable
import io.reactivex.Single
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEditEntity
import me.rei_m.hbfavmaterial.domain.entity.OAuthTokenEntity

interface HatenaService {

    companion object {
        const val TAG_READ_AFTER = "あとで読む"
    }

    fun fetchRequestToken(): Single<String>

    fun fetchAccessToken(requestToken: String): Single<OAuthTokenEntity>

    fun findBookmarkByUrl(oauthTokenEntity: OAuthTokenEntity,
                          urlString: String): Single<BookmarkEditEntity>

    fun upsertBookmark(oauthTokenEntity: OAuthTokenEntity,
                       urlString: String,
                       comment: String,
                       isOpen: Boolean,
                       tags: List<String> = listOf()): Single<BookmarkEditEntity>

    fun deleteBookmark(oauthTokenEntity: OAuthTokenEntity,
                       urlString: String): Completable
}
