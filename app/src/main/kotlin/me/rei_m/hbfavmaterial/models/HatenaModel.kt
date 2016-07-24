package me.rei_m.hbfavmaterial.models

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import me.rei_m.hbfavmaterial.entities.BookmarkEditEntity
import me.rei_m.hbfavmaterial.entities.OAuthTokenEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.network.HatenaDeleteBookmarkLoadedEvent
import me.rei_m.hbfavmaterial.events.network.HatenaPostBookmarkLoadedEvent
import me.rei_m.hbfavmaterial.events.network.LoadedEventStatus
import me.rei_m.hbfavmaterial.extensions.getAppPreferences
import me.rei_m.hbfavmaterial.repositories.HatenaRepository
import retrofit2.adapter.rxjava.HttpException
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.HttpURLConnection

/**
 * はてなのOAuth関連の情報を管理するModel.
 */
class HatenaModel {

    var isBusy = false
        private set

    var oauthTokenEntity: OAuthTokenEntity = OAuthTokenEntity()
        private set

    private val mHatenaRepository: HatenaRepository

    companion object {

        val TAG_READ_AFTER = "あとで読む"

        private val KEY_PREF_OAUTH = "KEY_PREF_OAUTH"
    }

    constructor(context: Context, hatenaRepository: HatenaRepository) {

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

        mHatenaRepository.upsertBookmark(oauthTokenEntity, url, comment, isOpen, tags)
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

        val observer = object : Observer<Void?> {

            override fun onNext(t: Void?) {
            }

            override fun onCompleted() {
                isBusy = false
                EventBusHolder.EVENT_BUS.post(HatenaDeleteBookmarkLoadedEvent(LoadedEventStatus.OK))
            }

            override fun onError(e: Throwable?) {
                isBusy = false

                if (e is HttpException) {
                    if (e.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                        EventBusHolder.EVENT_BUS.post(HatenaDeleteBookmarkLoadedEvent(LoadedEventStatus.NOT_FOUND))
                        return
                    }
                }
                EventBusHolder.EVENT_BUS.post(HatenaDeleteBookmarkLoadedEvent(LoadedEventStatus.ERROR))
            }
        }

        mHatenaRepository.deleteBookmark(oauthTokenEntity, url)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }
    
    /**
     * Preferencesを取得する
     */
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getAppPreferences(HatenaModel::class.java.simpleName)
    }
}
