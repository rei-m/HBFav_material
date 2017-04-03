package me.rei_m.hbfavmaterial.domain.service.impl

import io.reactivex.Completable
import io.reactivex.Single
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEditEntity
import me.rei_m.hbfavmaterial.domain.entity.OAuthTokenEntity
import me.rei_m.hbfavmaterial.domain.service.HatenaService
import me.rei_m.hbfavmaterial.exception.NetworkFailureException
import me.rei_m.hbfavmaterial.infra.exeption.HTTPException
import me.rei_m.hbfavmaterial.infra.network.HatenaOAuthApiService
import me.rei_m.hbfavmaterial.infra.network.HatenaOAuthManager
import me.rei_m.hbfavmaterial.infra.network.RetrofitManager
import retrofit2.HttpException
import java.net.HttpURLConnection

class HatenaServiceImpl(private val hatenaOAuthManager: HatenaOAuthManager) : HatenaService {

    companion object {
        private const val MAX_TAGS_COUNT = 10
    }

    override fun fetchRequestToken(): Single<String> {
        return Single.create { t ->
            val authUrl = hatenaOAuthManager.retrieveRequestToken()
            if (authUrl != null) {
                t.onSuccess(authUrl)
            } else {
                t.onError(HTTPException(HttpURLConnection.HTTP_UNAUTHORIZED))
            }
        }
    }

    override fun fetchAccessToken(requestToken: String): Single<OAuthTokenEntity> {
        return Single.create { t ->
            if (hatenaOAuthManager.retrieveAccessToken(requestToken)) {
                t.onSuccess(OAuthTokenEntity(hatenaOAuthManager.consumer.token, hatenaOAuthManager.consumer.tokenSecret))
            } else {
                t.onError(HTTPException(HttpURLConnection.HTTP_UNAUTHORIZED))
            }
        }
    }

    override fun findBookmarkByUrl(oauthTokenEntity: OAuthTokenEntity,
                                   urlString: String): Single<BookmarkEditEntity> {

        hatenaOAuthManager.consumer.setTokenWithSecret(oauthTokenEntity.token, oauthTokenEntity.secretToken)

        val retrofit = RetrofitManager.createOAuthRetrofit(hatenaOAuthManager.consumer)

        return retrofit.create(HatenaOAuthApiService::class.java).getBookmark(urlString).map { (_, private, _, _, tags, _, comment) ->
            return@map BookmarkEditEntity(url = urlString,
                    isFirstEdit = false,
                    comment = comment,
                    isPrivate = private,
                    tags = tags)
        }.onErrorResumeNext {
            return@onErrorResumeNext if (it is HttpException) {
                when (it.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        Single.just(BookmarkEditEntity(url = urlString, isFirstEdit = true))
                    }
                    else -> {
                        Single.error(NetworkFailureException())
                    }
                }
            } else {
                Single.error(it)
            }
        }
    }

    override fun upsertBookmark(oauthTokenEntity: OAuthTokenEntity,
                                urlString: String,
                                comment: String,
                                isOpen: Boolean,
                                tags: List<String>): Single<BookmarkEditEntity> {

        require(tags.size <= MAX_TAGS_COUNT) { "登録可能なタグは $MAX_TAGS_COUNT 個までです。" }

        hatenaOAuthManager.consumer.setTokenWithSecret(oauthTokenEntity.token, oauthTokenEntity.secretToken)

        val retrofit = RetrofitManager.createOAuthRetrofit(hatenaOAuthManager.consumer)

        return retrofit.create(HatenaOAuthApiService::class.java).postBookmark(urlString, comment, if (isOpen) "0" else "1", tags.toTypedArray())
                .map { (_, private, _, _, tags1, _, comment1) ->
                    return@map BookmarkEditEntity(url = urlString,
                            isFirstEdit = false,
                            comment = comment1,
                            isPrivate = private,
                            tags = tags1)
                }
    }

    override fun deleteBookmark(oauthTokenEntity: OAuthTokenEntity,
                                urlString: String): Completable {

        hatenaOAuthManager.consumer.setTokenWithSecret(oauthTokenEntity.token, oauthTokenEntity.secretToken)

        val retrofit = RetrofitManager.createOAuthRetrofit(hatenaOAuthManager.consumer)

        return retrofit.create(HatenaOAuthApiService::class.java)
                .deleteBookmark(urlString)
    }
}