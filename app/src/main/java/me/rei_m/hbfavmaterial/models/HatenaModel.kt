package me.rei_m.hbfavmaterial.models

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import me.rei_m.hbfavmaterial.di.ForApplication
import me.rei_m.hbfavmaterial.entities.BookmarkEditEntity
import me.rei_m.hbfavmaterial.entities.OAuthTokenEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.network.*
import me.rei_m.hbfavmaterial.exeptions.HTTPException
import me.rei_m.hbfavmaterial.extensions.getAppPreferences
import me.rei_m.hbfavmaterial.repositories.HatenaRepository
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.HttpURLConnection
import javax.inject.Inject

/**
 * はてなのOAuth関連の情報を管理するModel.
 */
class HatenaModel {

    var isBusy = false
        private set

    var oauthTokenEntity: OAuthTokenEntity? = null
        private set

    private val mHatenaRepository: HatenaRepository

    companion object {

        val TAG_READ_AFTER = "あとで読む"

        private val KEY_PREF_OAUTH = "KEY_PREF_OAUTH"
    }

    @Inject
    constructor(@ForApplication context: Context, hatenaRepository: HatenaRepository) {

        // Repositoryを作成する.
        this.mHatenaRepository = hatenaRepository

        // Preferencesからアクセストークンを復元する.
        val pref = getPreferences(context)
        val oauthJsonString = pref.getString(KEY_PREF_OAUTH, null)
        if (oauthJsonString != null) {
            oauthTokenEntity = Gson().fromJson(oauthJsonString, OAuthTokenEntity::class.java)
        }
    }

    /**
     * OAuth認証済か判定する.
     */
    fun isAuthorised(): Boolean {
        return (!(oauthTokenEntity?.token.isNullOrEmpty() || oauthTokenEntity?.secretToken.isNullOrEmpty()))
    }

    /**
     * OAuth認証用のリクエストトークンを取得する.
     */
    fun fetchRequestToken() {

        if (isBusy) {
            return
        }

        isBusy = true

        var requestUrl: String = ""

        val observer = object : Observer<String> {

            override fun onNext(t: String?) {
                requestUrl = t!!
            }

            override fun onCompleted() {
                isBusy = false
                EventBusHolder.EVENT_BUS.post(HatenaOAuthRequestTokenLoadedEvent(LoadedEventStatus.OK, requestUrl))
            }

            override fun onError(e: Throwable?) {
                isBusy = false
                EventBusHolder.EVENT_BUS.post(HatenaOAuthRequestTokenLoadedEvent(LoadedEventStatus.ERROR))
            }
        }

        mHatenaRepository.fetchRequestToken()
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }

    /**
     * OAuth認証用のAccessTokenを取得する.
     */
    fun fetchAccessToken(context: Context, requestToken: String) {

        if (isBusy) {
            return
        }

        isBusy = true

        val observer = object : Observer<OAuthTokenEntity> {

            override fun onNext(t: OAuthTokenEntity?) {
                oauthTokenEntity = t
            }

            override fun onCompleted() {
                isBusy = false
                // 成功した場合はTokenをPreferencesに保存.
                saveToken(context)
                EventBusHolder.EVENT_BUS.post(HatenaOAuthAccessTokenLoadedEvent(LoadedEventStatus.OK))
            }

            override fun onError(e: Throwable?) {
                isBusy = false
                EventBusHolder.EVENT_BUS.post(HatenaOAuthAccessTokenLoadedEvent(LoadedEventStatus.ERROR))
            }
        }

        mHatenaRepository.fetchAccessToken(requestToken)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }

    /**
     * 保存しているAccessTokenを削除する.
     */
    fun deleteAccessToken(context: Context) {
        oauthTokenEntity = OAuthTokenEntity()
        saveToken(context)
    }

    /**
     * ブックマーク情報を取得する.
     */
    fun fetchBookmark(url: String) {

        if (isBusy) {
            return
        }

        isBusy = true

        var bookmark: BookmarkEditEntity? = null

        val observer = object : Observer<BookmarkEditEntity> {

            override fun onNext(t: BookmarkEditEntity?) {
                bookmark = t
            }

            override fun onCompleted() {
                isBusy = false
                EventBusHolder.EVENT_BUS.post(HatenaGetBookmarkLoadedEvent(bookmark, LoadedEventStatus.OK))
            }

            override fun onError(e: Throwable?) {
                isBusy = false
                val error = e as HTTPException
                if (error.statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                    EventBusHolder.EVENT_BUS.post(HatenaGetBookmarkLoadedEvent(null, LoadedEventStatus.NOT_FOUND))
                } else {
                    EventBusHolder.EVENT_BUS.post(HatenaGetBookmarkLoadedEvent(null, LoadedEventStatus.ERROR))
                }
            }
        }

        mHatenaRepository.findBookmarkByUrl(oauthTokenEntity!!, url)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }

    /**
     * ブックマーク情報を登録する.
     */
    fun registerBookmark(url: String, comment: String, isOpen: Boolean, tags: List<String>) {

        if (isBusy) {
            return
        }

        isBusy = true

        var bookmark: BookmarkEditEntity? = null

        val observer = object : Observer<BookmarkEditEntity> {

            override fun onNext(t: BookmarkEditEntity?) {
                bookmark = t
            }

            override fun onCompleted() {
                isBusy = false
                EventBusHolder.EVENT_BUS.post(HatenaPostBookmarkLoadedEvent(bookmark, LoadedEventStatus.OK))
            }

            override fun onError(e: Throwable?) {
                isBusy = false
                EventBusHolder.EVENT_BUS.post(HatenaPostBookmarkLoadedEvent(null, LoadedEventStatus.ERROR))
            }
        }

        mHatenaRepository.upsertBookmark(oauthTokenEntity!!, url, comment, isOpen, tags)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }

    /**
     * ブックマーク情報を削除する.
     */
    fun deleteBookmark(url: String) {

        if (isBusy) {
            return
        }

        isBusy = true

        val observer = object : Observer<Boolean> {

            override fun onNext(t: Boolean?) {
            }

            override fun onCompleted() {
                isBusy = false
                EventBusHolder.EVENT_BUS.post(HatenaDeleteBookmarkLoadedEvent(LoadedEventStatus.OK))
            }

            override fun onError(e: Throwable?) {
                isBusy = false
                val error = e as HTTPException
                if (error.statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                    EventBusHolder.EVENT_BUS.post(HatenaDeleteBookmarkLoadedEvent(LoadedEventStatus.NOT_FOUND))
                } else {
                    EventBusHolder.EVENT_BUS.post(HatenaDeleteBookmarkLoadedEvent(LoadedEventStatus.ERROR))
                }
            }
        }

        mHatenaRepository.deleteBookmark(oauthTokenEntity!!, url)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }

    /**
     * PreferencesにModel内のアクセストークンを保存する.
     */
    private fun saveToken(context: Context) {
        getPreferences(context)
                .edit()
                .putString(KEY_PREF_OAUTH, Gson().toJson(oauthTokenEntity))
                .apply()
    }

    /**
     * Preferencesを取得する
     */
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getAppPreferences(HatenaModel::class.java.simpleName)
    }
}
