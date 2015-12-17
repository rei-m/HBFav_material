package me.rei_m.hbfavmaterial.models

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import me.rei_m.hbfavmaterial.entities.HatenaRestApiBookmarkResponse
import me.rei_m.hbfavmaterial.entities.OAuthTokenEntity
import me.rei_m.hbfavmaterial.events.*
import me.rei_m.hbfavmaterial.exeptions.HTTPException
import me.rei_m.hbfavmaterial.extensions.getAppPreferences
import me.rei_m.hbfavmaterial.extensions.getAssetToJson
import me.rei_m.hbfavmaterial.network.HatenaOAuthApi
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.HttpURLConnection

public class HatenaModel {

    public var isBusy = false
        private set

    public var oauthTokenEntity: OAuthTokenEntity? = null
        private set

    private var mHatenaOAuthApi: HatenaOAuthApi? = null

    companion object {
        private final val KEY_PREF_OAUTH = "KEY_PREF_OAUTH"
    }

    constructor(context: Context) {
        val hatenaJson = context.getAssetToJson("hatena.json")
        mHatenaOAuthApi = HatenaOAuthApi(hatenaJson.getString("consumer_key"), hatenaJson.getString("consumer_secret"))

        val pref = getPreferences(context)
        val oauthJsonString = pref.getString(KEY_PREF_OAUTH, null)
        if (oauthJsonString != null) {
            oauthTokenEntity = Gson().fromJson(oauthJsonString, OAuthTokenEntity::class.java)
        }
    }

    public fun isAuthorised(): Boolean {
        return (!(oauthTokenEntity?.token.isNullOrEmpty() || oauthTokenEntity?.secretToken.isNullOrEmpty()))
    }

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
                .finallyDo({
                    isBusy = false
                })
                .subscribe(observer)
    }

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
                .finallyDo({
                    isBusy = false
                })
                .subscribe(observer)
    }

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
                .finallyDo({
                    isBusy = false
                })
                .subscribe(observer)
    }

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
                .finallyDo({
                    isBusy = false
                })
                .subscribe(observer)

    }

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
                .finallyDo({
                    isBusy = false
                })
                .subscribe(observer)

    }

    private fun saveToken(context: Context) {
        getPreferences(context)
                .edit()
                .putString(KEY_PREF_OAUTH, Gson().toJson(oauthTokenEntity))
                .apply()
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getAppPreferences(HatenaModel::class.java.simpleName)
    }
}