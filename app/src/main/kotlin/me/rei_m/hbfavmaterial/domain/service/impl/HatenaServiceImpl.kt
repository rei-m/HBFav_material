package me.rei_m.hbfavmaterial.domain.service.impl

import me.rei_m.hbfavmaterial.domain.entity.BookmarkEditEntity
import me.rei_m.hbfavmaterial.domain.entity.OAuthTokenEntity
import me.rei_m.hbfavmaterial.domain.service.HatenaService
import me.rei_m.hbfavmaterial.infra.exeption.HTTPException
import me.rei_m.hbfavmaterial.infra.network.HatenaOAuthApiService
import me.rei_m.hbfavmaterial.infra.network.HatenaOAuthManager
import me.rei_m.hbfavmaterial.infra.network.RetrofitManager
import rx.Observable
import java.net.HttpURLConnection

class HatenaServiceImpl(private val hatenaOAuthManager: HatenaOAuthManager) : HatenaService {

    companion object {
        private const val MAX_TAGS_COUNT = 10
    }

    override fun fetchRequestToken(): Observable<String> {
        return Observable.create { t ->
            val authUrl = hatenaOAuthManager.retrieveRequestToken()
            if (authUrl != null) {
                t.onNext(authUrl)
                t.onCompleted()
            } else {
                t.onError(HTTPException(HttpURLConnection.HTTP_UNAUTHORIZED))
            }
        }
    }

    override fun fetchAccessToken(requestToken: String): Observable<OAuthTokenEntity> {
        return Observable.create { t ->
            if (hatenaOAuthManager.retrieveAccessToken(requestToken)) {
                t.onNext(OAuthTokenEntity(hatenaOAuthManager.consumer.token, hatenaOAuthManager.consumer.tokenSecret))
                t.onCompleted()
            } else {
                t.onError(HTTPException(HttpURLConnection.HTTP_UNAUTHORIZED))
            }
        }
    }

    override fun findBookmarkByUrl(oauthTokenEntity: OAuthTokenEntity,
                                   urlString: String): Observable<BookmarkEditEntity> {

        hatenaOAuthManager.consumer.setTokenWithSecret(oauthTokenEntity.token, oauthTokenEntity.secretToken)

        val retrofit = RetrofitManager.createOAuthRetrofit(hatenaOAuthManager.consumer)

        return retrofit.create(HatenaOAuthApiService::class.java)
                .getBookmark(urlString)
                .map {
                    response ->
                    return@map BookmarkEditEntity(url = urlString,
                            comment = response.comment,
                            isPrivate = response.private,
                            tags = response.tags)
                }
    }

    override fun upsertBookmark(oauthTokenEntity: OAuthTokenEntity,
                                urlString: String,
                                comment: String,
                                isOpen: Boolean,
                                tags: List<String>): Observable<BookmarkEditEntity> {

        require(tags.size <= MAX_TAGS_COUNT) { "登録可能なタグは ${MAX_TAGS_COUNT} 個までです。" }

        hatenaOAuthManager.consumer.setTokenWithSecret(oauthTokenEntity.token, oauthTokenEntity.secretToken)

        val retrofit = RetrofitManager.createOAuthRetrofit(hatenaOAuthManager.consumer)

        return retrofit.create(HatenaOAuthApiService::class.java)
                .postBookmark(urlString, comment, if (isOpen) "0" else "1", tags.toTypedArray())
                .map {
                    response ->
                    return@map BookmarkEditEntity(url = urlString,
                            comment = response.comment,
                            isPrivate = response.private,
                            tags = response.tags)
                }
    }

    override fun deleteBookmark(oauthTokenEntity: OAuthTokenEntity,
                                urlString: String): Observable<Void?> {

        hatenaOAuthManager.consumer.setTokenWithSecret(oauthTokenEntity.token, oauthTokenEntity.secretToken)

        val retrofit = RetrofitManager.createOAuthRetrofit(hatenaOAuthManager.consumer)

        return retrofit.create(HatenaOAuthApiService::class.java)
                .deleteBookmark(urlString)
    }
}