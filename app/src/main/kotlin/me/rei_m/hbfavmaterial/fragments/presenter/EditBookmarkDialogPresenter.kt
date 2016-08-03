package me.rei_m.hbfavmaterial.fragments.presenter

import android.content.Context
import android.support.v4.app.DialogFragment
import me.rei_m.hbfavmaterial.entities.BookmarkEditEntity
import me.rei_m.hbfavmaterial.extensions.getAppContext
import me.rei_m.hbfavmaterial.repositories.HatenaTokenRepository
import me.rei_m.hbfavmaterial.repositories.TwitterSessionRepository
import me.rei_m.hbfavmaterial.service.HatenaService
import me.rei_m.hbfavmaterial.service.TwitterService
import me.rei_m.hbfavmaterial.utils.BookmarkUtil
import retrofit2.adapter.rxjava.HttpException
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.HttpURLConnection
import javax.inject.Inject

class EditBookmarkDialogPresenter(private val view: EditBookmarkDialogContact.View) : EditBookmarkDialogContact.Actions {

    @Inject
    lateinit var hatenaTokenRepository: HatenaTokenRepository

    @Inject
    lateinit var hatenaService: HatenaService

    @Inject
    lateinit var twitterSessionRepository: TwitterSessionRepository

    @Inject
    lateinit var twitterService: TwitterService

    private val appContext: Context
        get() = (view as DialogFragment).getAppContext()

    private var isLoading = false

    override fun onViewCreated() {
        view.setSwitchShareTwitterCheck(twitterSessionRepository.resolve().isShare)
    }

    override fun changeCheckedShareTwitter(isChecked: Boolean) {
        val twitterSessionEntity = twitterSessionRepository.resolve()
        if (isChecked) {
            if (!twitterSessionEntity.oAuthTokenEntity.isAuthorised) {
                view.startSettingActivity()
                view.dismissDialog()
                return
            }
        }
        twitterSessionEntity.isShare = isChecked
        twitterSessionRepository.store(appContext, twitterSessionEntity)
    }

    override fun registerBookmark(url: String,
                                  title: String,
                                  comment: String,
                                  isOpen: Boolean,
                                  tags: List<String>,
                                  isShareAtTwitter: Boolean): Subscription? {

        if (isLoading) return null

        if (isShareAtTwitter) {
            twitterService.postTweet(BookmarkUtil.createShareText(url, title, comment))
        }

        val oAuthTokenEntity = hatenaTokenRepository.resolve()

        isLoading = true
        view.showProgress()

        return hatenaService.upsertBookmark(oAuthTokenEntity, url, comment, isOpen, tags)
                .doOnUnsubscribe {
                    isLoading = false
                    view.hideProgress()
                }
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onUpsertBookmarkSuccess(it)
                }, {
                    onUpsertBookmarkFailure(it)
                })
    }

    private fun onUpsertBookmarkSuccess(bookmarkEditEntity: BookmarkEditEntity) {
        view.dismissDialog()
    }

    private fun onUpsertBookmarkFailure(e: Throwable?) {
        view.showNetworkErrorMessage()
    }

    override fun deleteBookmark(bookmarkUrl: String): Subscription? {

        if (isLoading) return null

        val oAuthTokenEntity = hatenaTokenRepository.resolve()

        isLoading = true
        view.showProgress()

        return hatenaService.deleteBookmark(oAuthTokenEntity, bookmarkUrl)
                .doOnUnsubscribe {
                    isLoading = false
                    view.hideProgress()
                }
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onDeleteBookmarkSuccess(it)
                }, {
                    onDeleteBookmarkFailure(it)
                })
    }

    private fun onDeleteBookmarkSuccess(void: Void?) {
        view.dismissDialog()
    }

    private fun onDeleteBookmarkFailure(e: Throwable?) {
        if (e is HttpException) {
            if (e.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                view.dismissDialog()
                return
            }
        }
        view.showNetworkErrorMessage()
    }
}
