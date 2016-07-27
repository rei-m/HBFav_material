package me.rei_m.hbfavmaterial.fragments.presenter

import me.rei_m.hbfavmaterial.entities.BookmarkEditEntity
import me.rei_m.hbfavmaterial.repositories.HatenaTokenRepository
import me.rei_m.hbfavmaterial.service.HatenaService
import me.rei_m.hbfavmaterial.service.TwitterService
import me.rei_m.hbfavmaterial.utils.BookmarkUtil
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class EditBookmarkDialogPresenter(private val view: EditBookmarkDialogContact.View) : EditBookmarkDialogContact.Actions {

    @Inject
    lateinit var hatenaTokenRepository: HatenaTokenRepository

    @Inject
    lateinit var hatenaService: HatenaService

    @Inject
    lateinit var twitterService: TwitterService

    private var isLoading = false

    override fun changeCheckedShareTwitter(isChecked: Boolean) {

    }

    override fun changeCheckedDelete(isChecked: Boolean) {

    }

    override fun registerBookmark(url: String,
                                  title: String,
                                  comment: String,
                                  isOpen: Boolean,
                                  tags: List<String>,
                                  isShareAtTwitter: Boolean): Subscription? {

        if (isLoading) return null

        view.showProgress()

        isLoading = true

        val observer = object : Observer<BookmarkEditEntity> {

            override fun onNext(t: BookmarkEditEntity?) {
                view.dismiss()
            }

            override fun onCompleted() {
            }

            override fun onError(e: Throwable?) {
                view.showNetworkErrorMessage()
            }
        }


        if (isShareAtTwitter) {
            twitterService.postTweet(BookmarkUtil.createShareText(url, title, comment))
        }

        val oAuthTokenEntity = hatenaTokenRepository.resolve()

        return hatenaService.upsertBookmark(oAuthTokenEntity, url, comment, isOpen, tags)
                .doOnUnsubscribe {
                    isLoading = false
                    view.hideProgress()
                }
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }

    override fun deleteBookmark(bookmarkUrl: String): Subscription {

    }
}
