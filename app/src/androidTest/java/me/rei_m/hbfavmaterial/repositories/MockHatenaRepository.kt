package me.rei_m.hbfavmaterial.repositories

import android.content.Context
import me.rei_m.hbfavmaterial.entities.BookmarkEditEntity
import me.rei_m.hbfavmaterial.entities.OAuthTokenEntity
import rx.Observable

class MockHatenaRepository(context: Context) : HatenaRepository(context) {

    companion object {
        val REQUEST_URL = "REQUEST_URL"
        val TOKEN = "TOKEN"
        val SECRET_TOKEN = "SECRET_TOKEN"
    }

    override fun fetchRequestToken(): Observable<String> {
        return Observable.just(REQUEST_URL)
    }

    override fun fetchAccessToken(requestToken: String): Observable<OAuthTokenEntity> {
        return Observable.just(OAuthTokenEntity(TOKEN, SECRET_TOKEN))
    }

    override fun findBookmarkByUrl(oauthTokenEntity: OAuthTokenEntity, urlString: String): Observable<BookmarkEditEntity> {
        return Observable.just(BookmarkEditEntity("", "", false))
    }

    override fun upsertBookmark(oauthTokenEntity: OAuthTokenEntity, urlString: String, comment: String, isOpen: Boolean, tags: List<String>): Observable<BookmarkEditEntity> {
        return Observable.just(BookmarkEditEntity("", "", false))
    }

    override fun deleteBookmark(oauthTokenEntity: OAuthTokenEntity, urlString: String): Observable<Boolean> {
        return Observable.just(true)
    }
}
