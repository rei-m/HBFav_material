package me.rei_m.hbfavmaterial.presentation.fragment

import me.rei_m.hbfavmaterial.domain.entity.BookmarkEditEntity
import me.rei_m.hbfavmaterial.usecase.*
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.net.HttpURLConnection

class EditBookmarkDialogPresenter(private val getUserUsecase: GetUserUsecase,
                                  private val getTwitterSessionUsecase: GetTwitterSessionUsecase,
                                  private val updateUserUsecase: UpdateUserUsecase,
                                  private val updateTwitterSessionUsecase: UpdateTwitterSessionUsecase,
                                  private val registerBookmarkUsecase: RegisterBookmarkUsecase,
                                  private val deleteBookmarkUsecase: DeleteBookmarkUsecase) : EditBookmarkDialogContact.Actions {

    private lateinit var view: EditBookmarkDialogContact.View

    private lateinit var bookmarkUrl: String

    private lateinit var bookmarkTitle: String

    private var bookmarkEditEntity: BookmarkEditEntity? = null

    private var subscription: CompositeSubscription? = null

    private var isLoading = false

    override fun onCreate(view: EditBookmarkDialogContact.View,
                          bookmarkUrl: String,
                          bookmarkTitle: String,
                          bookmarkEditEntity: BookmarkEditEntity?) {
        this.view = view
        this.bookmarkUrl = bookmarkUrl
        this.bookmarkTitle = bookmarkTitle
        this.bookmarkEditEntity = bookmarkEditEntity
    }

    override fun onViewCreated() {
        val userEntity = getUserUsecase.get()
        view.setSwitchOpenCheck(userEntity.isCheckedPostBookmarkOpen)
        view.setSwitchShareTwitterCheck(getTwitterSessionUsecase.get().isShare)
        view.setSwitchReadAfterCheck(userEntity.isCheckedPostBookmarkReadAfter)
    }

    override fun onResume() {
        subscription = CompositeSubscription()
    }

    override fun onPause() {
        subscription?.unsubscribe()
        subscription = null
    }

    override fun onCheckedChangeOpen(isChecked: Boolean) {
        updateUserUsecase.updateIsCheckedPostBookmarkOpen(isChecked)
        view.setSwitchOpenCheck(isChecked)
    }

    override fun onCheckedChangeShareTwitter(isChecked: Boolean) {
        if (isChecked) {
            if (!getTwitterSessionUsecase.get().oAuthTokenEntity.isAuthorised) {
                view.startSettingActivity()
                view.dismissDialog()
                return
            }
        }
        updateTwitterSessionUsecase.updateIsShare(isChecked)
    }

    override fun onCheckedChangeReadAfter(isChecked: Boolean) {
        updateUserUsecase.updateIsCheckedPostBookmarkReadAfter(isChecked)
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

        isLoading = true
        view.showProgress()

        val tags = bookmarkEditEntity?.tags?.toMutableList() ?: mutableListOf()

        subscription?.add(registerBookmarkUsecase.register(url, title, comment, tags, isOpen, isCheckedReadAfter, isShareAtTwitter)
                .doOnUnsubscribe {
                    isLoading = false
                    view.hideProgress()
                }
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.dismissDialog()
                }, {
                    view.showNetworkErrorMessage()
                }))
    }

    private fun deleteBookmark(bookmarkUrl: String) {

        if (isLoading) return

        isLoading = true
        view.showProgress()

        subscription?.add(deleteBookmarkUsecase.delete(bookmarkUrl)
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

    private fun onDeleteBookmarkSuccess(@Suppress("unused") unit: Unit) {
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
