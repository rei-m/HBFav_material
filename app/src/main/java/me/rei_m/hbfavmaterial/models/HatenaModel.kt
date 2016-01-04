package me.rei_m.hbfavmaterial.models

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import me.rei_m.hbfavmaterial.entities.HatenaRestApiBookmarkResponse
import me.rei_m.hbfavmaterial.entities.OAuthTokenEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.network.*
import me.rei_m.hbfavmaterial.exeptions.HTTPException
import me.rei_m.hbfavmaterial.extensions.getAppPreferences
import me.rei_m.hbfavmaterial.extensions.getAssetToJson
import me.rei_m.hbfavmaterial.network.HatenaOAuthApi
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.HttpURLConnection

/**
 * はてなのOAuth関連の情報を管理するModel.
 */
public class HatenaModel {

    public var isBusy = false
        private set

    public var oauthTokenEntity: OAuthTokenEntity? = null
        private set

    private var mHatenaOAuthApi: HatenaOAuthApi? = null

    companion object {
        private val KEY_PREF_OAUTH = "KEY_PREF_OAUTH"
    }

    /**
     * コンストラクタ.
     */
    constructor(context: Context) {

        // OAuth認証用のキーを作成し、OAuthAPIを作成する.
        val hatenaJson = context.getAssetToJson("hatena.json")
        mHatenaOAuthApi = HatenaOAuthApi(hatenaJson.getString("consumer_key"), hatenaJson.getString("consumer_secret"))

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
    public fun isAuthorised(): Boolean {
        return (!(oauthTokenEntity?.token.isNullOrEmpty() || oauthTokenEntity?.secretToken.isNullOrEmpty()))
    }

    /**
     * OAuth認証用のリクエストトークンを取得する.
     */
    public fun fetchRequestToken() {

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
                EventBusHolder.EVENT_BUS.post(HatenaOAuthRequestTokenLoadedEvent(LoadedEventStatus.OK, requestUrl))
            }

            override fun onError(e: Throwable?) {
                EventBusHolder.EVENT_BUS.post(HatenaOAuthRequestTokenLoadedEvent(LoadedEventStatus.ERROR))
            }
        }

        mHatenaOAuthApi!!.requestRequestToken()
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo { isBusy = false }
                .subscribe(observer)
    }

    /**
     * OAuth認証用のAccessTokenを取得する.
     */
    public fun fetchAccessToken(context: Context, requestToken: String) {

        if (isBusy) {
            return
        }

        isBusy = true

        val observer = object : Observer<OAuthTokenEntity> {

            override fun onNext(t: OAuthTokenEntity?) {
                oauthTokenEntity = t
            }

            override fun onCompleted() {
                // 成功した場合はTokenをPreferencesに保存.
                saveToken(context)
                EventBusHolder.EVENT_BUS.post(HatenaOAuthAccessTokenLoadedEvent(LoadedEventStatus.OK))
            }

            override fun onError(e: Throwable?) {
                EventBusHolder.EVENT_BUS.post(HatenaOAuthAccessTokenLoadedEvent(LoadedEventStatus.ERROR))
            }
        }

        mHatenaOAuthApi!!.requestAccessToken(requestToken)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo { isBusy = false }
                .subscribe(observer)
    }

    /**
     * 保存しているAccessTokenを削除する.
     */
    public fun deleteAccessToken(context: Context) {
        oauthTokenEntity = OAuthTokenEntity()
        saveToken(context)
    }

    /**
     * ブックマーク情報を取得する.
     */
    public fun fetchBookmark(url: String) {

        if (isBusy) {
            return
        }

        isBusy = true

        var response: HatenaRestApiBookmarkResponse? = null

        val observer = object : Observer<HatenaRestApiBookmarkResponse> {

            override fun onNext(t: HatenaRestApiBookmarkResponse?) {
                response = t
            }

            override fun onCompleted() {
                EventBusHolder.EVENT_BUS.post(HatenaGetBookmarkLoadedEvent(response, LoadedEventStatus.OK))
            }

            override fun onError(e: Throwable?) {
                val error = e as HTTPException
                if (error.statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                    EventBusHolder.EVENT_BUS.post(HatenaGetBookmarkLoadedEvent(null, LoadedEventStatus.NOT_FOUND))
                } else {
                    EventBusHolder.EVENT_BUS.post(HatenaGetBookmarkLoadedEvent(null, LoadedEventStatus.ERROR))
                }
            }
        }

        mHatenaOAuthApi!!.getBookmark(oauthTokenEntity!!, url)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo { isBusy = false }
                .subscribe(observer)
    }

    /**
     * ブックマーク情報を登録する.
     */
    public fun registerBookmark(url: String, comment: String, isOpen: Boolean) {

        if (isBusy) {
            return
        }

        isBusy = true

        var response: HatenaRestApiBookmarkResponse? = null

        val observer = object : Observer<HatenaRestApiBookmarkResponse> {

            override fun onNext(t: HatenaRestApiBookmarkResponse?) {
                response = t
            }

            override fun onCompleted() {
                EventBusHolder.EVENT_BUS.post(HatenaPostBookmarkLoadedEvent(response, LoadedEventStatus.OK))
            }

            override fun onError(e: Throwable?) {
                EventBusHolder.EVENT_BUS.post(HatenaPostBookmarkLoadedEvent(null, LoadedEventStatus.ERROR))
            }
        }

        mHatenaOAuthApi!!.postBookmark(oauthTokenEntity!!, url, comment, isOpen)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo { isBusy = false }
                .subscribe(observer)

    }

    /**
     * ブックマーク情報を削除する.
     */
    public fun deleteBookmark(url: String) {

        if (isBusy) {
            return
        }

        isBusy = true

        val observer = object : Observer<String> {

            override fun onNext(t: String?) {
            }

            override fun onCompleted() {
                EventBusHolder.EVENT_BUS.post(HatenaDeleteBookmarkLoadedEvent(LoadedEventStatus.OK))
            }

            override fun onError(e: Throwable?) {
                val error = e as HTTPException
                if (error.statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                    EventBusHolder.EVENT_BUS.post(HatenaDeleteBookmarkLoadedEvent(LoadedEventStatus.NOT_FOUND))
                } else {
                    EventBusHolder.EVENT_BUS.post(HatenaDeleteBookmarkLoadedEvent(LoadedEventStatus.ERROR))
                }
            }
        }

        mHatenaOAuthApi!!.deleteBookmark(oauthTokenEntity!!, url)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo { isBusy = false }
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
