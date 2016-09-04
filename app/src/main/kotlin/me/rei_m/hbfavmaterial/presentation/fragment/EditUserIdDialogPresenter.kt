package me.rei_m.hbfavmaterial.presentation.fragment

import me.rei_m.hbfavmaterial.usecase.ConfirmExistingUserIdUsecase
import me.rei_m.hbfavmaterial.usecase.GetUserUsecase
import retrofit2.adapter.rxjava.HttpException
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.net.HttpURLConnection

class EditUserIdDialogPresenter(private val getUserUsecase: GetUserUsecase,
                                private val confirmExistingUserIdUsecase: ConfirmExistingUserIdUsecase) : EditUserIdDialogContact.Actions {

    private lateinit var view: EditUserIdDialogContact.View

    private var subscription: CompositeSubscription? = null

    private var isLoading = false

    override fun onCreate(view: EditUserIdDialogContact.View) {
        this.view = view
    }

    override fun onViewCreated() {
        view.setEditUserId(getUserUsecase.get().id)
    }

    override fun onResume() {
        subscription = CompositeSubscription()
    }

    override fun onPause() {
        subscription?.unsubscribe()
        subscription = null
    }

    override fun onClickButtonOk(userId: String) {

        val userEntity = getUserUsecase.get()

        if (!userEntity.isSameId(userId)) {
            subscription?.add(confirmExistingUserId(userId))
        } else {
            view.dismissDialog()
        }
    }

    private fun confirmExistingUserId(userId: String): Subscription? {

        if (isLoading) return null

        isLoading = true
        view.showProgress()

        return confirmExistingUserIdUsecase.confirm(userId)
                .doOnUnsubscribe {
                    isLoading = false
                    view.hideProgress()
                }
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onConfirmExistingUserIdSuccess(it)
                }, {
                    onConfirmExistingUserIdFailure(it)
                })
    }

    private fun onConfirmExistingUserIdSuccess(isValid: Boolean) {
        if (isValid) {
            view.dismissDialog()
        } else {
            view.displayInvalidUserIdMessage()
        }
    }

    private fun onConfirmExistingUserIdFailure(e: Throwable) {
        if (e is HttpException) {
            if (e.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                view.displayInvalidUserIdMessage()
                return
            }
        }
        view.showNetworkErrorMessage()
    }
}
