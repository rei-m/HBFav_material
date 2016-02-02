package me.rei_m.hbfavmaterial.repositories

import android.content.Context
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.ForApplication
import me.rei_m.hbfavmaterial.entities.BookmarkEditEntity
import me.rei_m.hbfavmaterial.entities.OAuthTokenEntity
import me.rei_m.hbfavmaterial.network.HatenaOAuthApi
import rx.Observable
import java.util.*
import javax.inject.Inject

open class HatenaRepository {

    companion object {
        private val MAX_TAGS_COUNT = 10
    }

    /**
     * HatenaのOAuth認証が必要なAPIにアクセスするモジュール.
     */
    private val mHatenaOAuthApi: HatenaOAuthApi

    /**
     * コンストラクタ.
     */
    @Inject
    constructor(@ForApplication context: Context) {
        // OAuth認証用のキーを作成し、OAuthAPIを作成する.
        mHatenaOAuthApi = HatenaOAuthApi(context.getString(R.string.api_key_hatena_consumer_key),
                context.getString(R.string.api_key_hatena_consumer_secret))
    }

    /**
     * 認証用のRequestTokenを取得する.
     */
    open fun fetchRequestToken(): Observable<String> {
        return mHatenaOAuthApi
                .requestRequestToken()
    }

    /**
     * 認証用のAccessTokenを取得する.
     */
    open fun fetchAccessToken(requestToken: String): Observable<OAuthTokenEntity> {
        return mHatenaOAuthApi
                .requestAccessToken(requestToken)
    }

    /**
     * ブックマーク情報をURLから検索する.
     */
    open fun findBookmarkByUrl(oauthTokenEntity: OAuthTokenEntity,
                               urlString: String): Observable<BookmarkEditEntity> {
        return mHatenaOAuthApi
                .getBookmark(oauthTokenEntity, urlString)
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
                            tags: List<String> = ArrayList<String>()): Observable<BookmarkEditEntity> {

        // Tagに登録できる上限を超えていたら例外.
        if (MAX_TAGS_COUNT < tags.size) {
            throw IllegalArgumentException("登録可能なタグは $MAX_TAGS_COUNT 個までです。")
        }

        return mHatenaOAuthApi.postBookmark(oauthTokenEntity, urlString, comment, isOpen, tags)
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
                            urlString: String): Observable<Boolean> {
        return mHatenaOAuthApi.deleteBookmark(oauthTokenEntity, urlString)
    }
}
