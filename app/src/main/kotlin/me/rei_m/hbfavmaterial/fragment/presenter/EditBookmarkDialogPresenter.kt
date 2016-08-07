package me.rei_m.hbfavmaterial.fragment.presenter

import android.content.Context
import android.support.v4.app.DialogFragment
import me.rei_m.hbfavmaterial.di.ActivityComponent
import me.rei_m.hbfavmaterial.entitiy.BookmarkEditEntity
import me.rei_m.hbfavmaterial.extension.getAppContext
import me.rei_m.hbfavmaterial.repository.HatenaTokenRepository
import me.rei_m.hbfavmaterial.repository.TwitterSessionRepository
import me.rei_m.hbfavmaterial.service.HatenaService
import me.rei_m.hbfavmaterial.service.TwitterService
import me.rei_m.hbfavmaterial.util.BookmarkUtil
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.net.HttpURLConnection
import javax.inject.Inject

class EditBookmarkDialogPresenter() : EditBookmarkDialogContact.Actions {

    @Inject
    lateinit var hatenaTokenRepository: HatenaTokenRepository

    @Inject
    lateinit var hatenaService: HatenaService

    @Inject
    lateinit var twitterSessionRepository: TwitterSessionRepository

    @Inject
    lateinit var twitterService: TwitterService

    private lateinit var view: EditBookmarkDialogContact.View

    private lateinit var bookmarkUrl: String

    private lateinit var bookmarkTitle: String

    private var bookmarkEditEntity: BookmarkEditEntity? = null

    private var subscription: CompositeSubscription? = null

    private val appContext: Context
        get() = (view as DialogFragment).getAppContext()

    private var isLoading = false

    override fun onCreate(component: ActivityComponent,
                          view: EditBookmarkDialogContact.View,
                          bookmarkUrl: String,
                          bookmarkTitle: String,
                          bookmarkEditEntity: BookmarkEditEntity?) {

        component.inject(this)
        this.view = view
        this.bookmarkUrl = bookmarkUrl
        this.bookmarkTitle = bookmarkTitle
        this.bookmarkEditEntity = bookmarkEditEntity
    }

    override fun onViewCreated() {
        view.setSwitchShareTwitterCheck(twitterSessionRepository.resolve().isShare)
    }

    override fun onResume() {
        subscription = CompositeSubscription()
    }

    override fun onPause() {
        subscription?.unsubscribe()
        subscription = null
    }

    override fun onCheckedChangeShareTwitter(isChecked: Boolean) {
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

    override fun onCheckedChangeDelete(isChecked: Boolean) {
        view.setSwitchEnableByDelete(!isChecked)
    }

    override fun onClickButtonOk(isCheckedDelete: Boolean,
                                 inputtedComment: String,
                                 isCheckedOpen: Boolean,
                                 isCheckedReadAfter: Boolean,
                                 isCheckedShareTwitter: Boolean) {

        if (isCheckedDelete) {
            deleteBookmark(bookmarkUrl)
        } else {
            registerBookmark(bookmarkUrl,
                    bookmarkTitle,
                    inputtedComment,
                    isCheckedOpen,
                    isCheckedReadAfter,
                    isCheckedShareTwitter)
        }
    }

    private fun registerBookmark(url: String,
                                 title: String,
                                 comment: String,
                                 isOpen: Boolean,
                                 isCheckedReadAfter: Boolean,
                                 isShareAtTwitter: Boolean) {

        if (isLoading) return

        if (isShareAtTwitter) {
            twitterService.postTweet(BookmarkUtil.createShareText(url, title, comment))
        }

        val oAuthTokenEntity = hatenaTokenRepository.resolve()

        val tags = bookmarkEditEntity?.tags?.toMutableList() ?: mutableListOf()

        if (isCheckedReadAfter) {
            if (!tags.contains(HatenaService.TAG_READ_AFTER)) {
                tags.add(HatenaService.TAG_READ_AFTER)
            }
        } else {
            if (tags.contains(HatenaService.TAG_READ_AFTER)) {
                tags.remove(HatenaService.TAG_READ_AFTER)
            }
        }

        isLoading = true
        view.showProgress()

        subscription?.add(hatenaService.upsertBookmark(oAuthTokenEntity, url, comment, isOpen, tags)
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
                }))
    }

    private fun onUpsertBookmarkSuccess(bookmarkEditEntity: BookmarkEditEntity) {
        view.dismissDialog()
    }

    private fun onUpsertBookmarkFailure(e: Throwable?) {
        view.showNetworkErrorMessage()
    }

    private fun deleteBookmark(bookmarkUrl: String) {

        if (isLoading) return

        val oAuthTokenEntity = hatenaTokenRepository.resolve()

        isLoading = true
        view.showProgress()

        subscription?.add(hatenaService.deleteBookmark(oAuthTokenEntity, bookmarkUrl)
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
                }))
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
