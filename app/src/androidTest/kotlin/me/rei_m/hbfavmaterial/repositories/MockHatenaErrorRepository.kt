package me.rei_m.hbfavmaterial.repositories

import android.content.Context
import me.rei_m.hbfavmaterial.entities.BookmarkEditEntity
import me.rei_m.hbfavmaterial.entities.OAuthTokenEntity
import me.rei_m.hbfavmaterial.exeptions.HTTPException
import me.rei_m.hbfavmaterial.utils.HttpExceptionFactory
import rx.Observable
import java.net.HttpURLConnection

class MockHatenaErrorRepository(context: Context) : HatenaRepository(context) {

    companion object {
        val BOOKMARK_URL_NOT_FOUND = "BOOKMARK_URL_NOT_FOUND"
        val BOOKMARK_URL_ERROR = "BOOKMARK_URL_ERROR"
    }

    override fun fetchRequestToken(): Observable<String> {
        return Observable.create { t ->
            t.onError(HTTPException(HttpURLConnection.HTTP_UNAUTHORIZED))
        }
    }

    override fun fetchAccessToken(requestToken: String): Observable<OAuthTokenEntity> {
        return Observable.create { t ->
            t.onError(HTTPException(HttpURLConnection.HTTP_UNAUTHORIZED))
        }
    }

    override fun findBookmarkByUrl(oauthTokenEntity: OAuthTokenEntity, urlString: String): Observable<BookmarkEditEntity> {
        return when (urlString) {
            BOOKMARK_URL_NOT_FOUND -> {
                Observable.create<BookmarkEditEntity> { t ->
                    t.onError(HttpExceptionFactory.create(HttpURLConnection.HTTP_NOT_FOUND))
                }
            }
            else -> {
                Observable.create<BookmarkEditEntity> { t ->
                    t.onError(HttpExceptionFactory.create(HttpURLConnection.HTTP_INTERNAL_ERROR))
                }
            }
        }
    }

    override fun upsertBookmark(oauthTokenEntity: OAuthTokenEntity, urlString: String, comment: String, isOpen: Boolean, tags: List<String>): Observable<BookmarkEditEntity> {
        return Observable.create<BookmarkEditEntity> { t ->
            t.onError(HttpExceptionFactory.create(HttpURLConnection.HTTP_INTERNAL_ERROR))
        }
    }

    override fun deleteBookmark(oauthTokenEntity: OAuthTokenEntity, urlString: String): Observable<Void?> {
        return when (urlString) {
            BOOKMARK_URL_NOT_FOUND -> {
                Observable.create<Void?> { t ->
                    t.onError(HttpExceptionFactory.create(HttpURLConnection.HTTP_NOT_FOUND))
                }
            }
            else -> {
                Observable.create<Void?> { t ->
                    t.onError(HttpExceptionFactory.create(HttpURLConnection.HTTP_INTERNAL_ERROR))
                }
            }
        }
    }
}
