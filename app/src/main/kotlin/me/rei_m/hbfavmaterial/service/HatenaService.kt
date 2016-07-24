package me.rei_m.hbfavmaterial.service

import me.rei_m.hbfavmaterial.entities.BookmarkEditEntity
import me.rei_m.hbfavmaterial.entities.OAuthTokenEntity
import rx.Observable

interface HatenaService {

    companion object {
        const val TAG_READ_AFTER = "あとで読む"
    }
    
    fun fetchRequestToken(): Observable<String>

    fun fetchAccessToken(requestToken: String): Observable<OAuthTokenEntity>

    fun findBookmarkByUrl(oauthTokenEntity: OAuthTokenEntity,
                          urlString: String): Observable<BookmarkEditEntity>

    fun upsertBookmark(oauthTokenEntity: OAuthTokenEntity,
                       urlString: String,
                       comment: String,
                       isOpen: Boolean,
                       tags: List<String> = listOf()): Observable<BookmarkEditEntity>

    fun deleteBookmark(oauthTokenEntity: OAuthTokenEntity,
                       urlString: String): Observable<Void?>
}
