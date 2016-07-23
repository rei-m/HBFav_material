package me.rei_m.hbfavmaterial.repositories

import android.content.Context
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.ForApplication
import me.rei_m.hbfavmaterial.entities.BookmarkEditEntity
import me.rei_m.hbfavmaterial.entities.OAuthTokenEntity
import me.rei_m.hbfavmaterial.exeptions.HTTPException
import me.rei_m.hbfavmaterial.network.HatenaOAuthApiService
import me.rei_m.hbfavmaterial.network.HatenaOAuthManager
import me.rei_m.hbfavmaterial.network.RetrofitManager
import rx.Observable
import java.net.HttpURLConnection
import javax.inject.Inject

open class HatenaRepository {

    companion object {
        private val MAX_TAGS_COUNT = 10
    }

    /**
     * HatenaのOAuth認証を管理するモジュール.
     */
    private val hatenaOAuthManager: HatenaOAuthManager

    /**
     * コンストラクタ.
     */
    @Inject
    constructor(@ForApplication context: Context) {
        hatenaOAuthManager = HatenaOAuthManager(context.getString(R.string.api_key_hatena_consumer_key),
                context.getString(R.string.api_key_hatena_consumer_secret))
    }

    /**
     * 認証用のRequestTokenを取得する.
     */
    open fun fetchRequestToken(): Observable<String> {
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

    /**
     * 認証用のAccessTokenを取得する.
     */
    open fun fetchAccessToken(requestToken: String): Observable<OAuthTokenEntity> {
        return Observable.create { t ->
            if (hatenaOAuthManager.retrieveAccessToken(requestToken)) {
                t.onNext(OAuthTokenEntity(hatenaOAuthManager.consumer.token, hatenaOAuthManager.consumer.tokenSecret))
                t.onCompleted()
            } else {
                t.onError(HTTPException(HttpURLConnection.HTTP_UNAUTHORIZED))
            }
        }
    }

    /**
     * ブックマーク情報をURLから検索する.
     */
    open fun findBookmarkByUrl(oauthTokenEntity: OAuthTokenEntity,
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

    /**
     * ブックマーク情報を追加または更新する.
     */
    open fun upsertBookmark(oauthTokenEntity: OAuthTokenEntity,
                            urlString: String,
                            comment: String,
                            isOpen: Boolean,
                            tags: List<String> = listOf()): Observable<BookmarkEditEntity> {

        // Tagに登録できる上限を超えていたら例外.
        if (MAX_TAGS_COUNT < tags.size) {
            throw IllegalArgumentException("登録可能なタグは $MAX_TAGS_COUNT 個までです。")
        }

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

    /**
     * ブックマーク情報を削除する.
     */
    open fun deleteBookmark(oauthTokenEntity: OAuthTokenEntity,
                            urlString: String): Observable<Void?> {

        hatenaOAuthManager.consumer.setTokenWithSecret(oauthTokenEntity.token, oauthTokenEntity.secretToken)

        val retrofit = RetrofitManager.createOAuthRetrofit(hatenaOAuthManager.consumer)

        return retrofit.create(HatenaOAuthApiService::class.java)
                .deleteBookmark(urlString)
    }
}
