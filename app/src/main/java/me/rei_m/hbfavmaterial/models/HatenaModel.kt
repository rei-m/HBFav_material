package me.rei_m.hbfavmaterial.models

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import me.rei_m.hbfavmaterial.entities.OAuthTokenEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.HatenaOAuthAccessTokenLoadedEvent
import me.rei_m.hbfavmaterial.events.HatenaOAuthRequestTokenLoadedEvent
import me.rei_m.hbfavmaterial.events.LoadedEventResult
import me.rei_m.hbfavmaterial.extensions.getAppPreferences
import me.rei_m.hbfavmaterial.extensions.getAssetToJson
import me.rei_m.hbfavmaterial.network.HatenaOAuthApi
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

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
        println(oauthTokenEntity)
        return (!(oauthTokenEntity?.token.isNullOrEmpty() || oauthTokenEntity?.secretToken.isNullOrEmpty()))
    }

    public fun fetchRequestToken() {

        var requestUrl: String = ""

        val observer = object : Observer<String> {

            override fun onNext(t: String?) {
                requestUrl = t!!
            }

            override fun onCompleted() {
                EventBusHolder.EVENT_BUS.post(HatenaOAuthRequestTokenLoadedEvent(LoadedEventResult.COMPLETE, requestUrl))
            }

            override fun onError(e: Throwable?) {
                EventBusHolder.EVENT_BUS.post(HatenaOAuthRequestTokenLoadedEvent(LoadedEventResult.ERROR))
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
        val observer = object : Observer<OAuthTokenEntity> {

            override fun onNext(t: OAuthTokenEntity?) {
                oauthTokenEntity = t
            }

            override fun onCompleted() {
                saveToken(context)
                EventBusHolder.EVENT_BUS.post(HatenaOAuthAccessTokenLoadedEvent(LoadedEventResult.COMPLETE))
            }

            override fun onError(e: Throwable?) {
                EventBusHolder.EVENT_BUS.post(HatenaOAuthAccessTokenLoadedEvent(LoadedEventResult.ERROR))
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
        val observer = object : Observer<String> {

            override fun onNext(t: String?) {

            }

            override fun onCompleted() {

            }

            override fun onError(e: Throwable?) {
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