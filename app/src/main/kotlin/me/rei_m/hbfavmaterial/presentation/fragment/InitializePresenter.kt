package me.rei_m.hbfavmaterial.presentation.fragment

import me.rei_m.hbfavmaterial.usecase.ConfirmExistingUserIdUsecase
import me.rei_m.hbfavmaterial.usecase.GetUserUsecase
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.net.HttpURLConnection

class InitializePresenter(private val getUserUsecase: GetUserUsecase,
                          private val confirmExistingUserIdUsecase: ConfirmExistingUserIdUsecase) : InitializeContact.Actions {

    private lateinit var view: InitializeContact.View

    private var subscription: CompositeSubscription? = null

    private var isLoading = false

    override fun onCreate(view: InitializeContact.View) {

        this.view = view

        val userEntity = getUserUsecase.get()
        if (userEntity.isCompleteSetting) {
            view.navigateToMain()
        }
    }

    override fun onResume() {
        subscription = CompositeSubscription()
    }

    override fun onPause() {
        subscription?.unsubscribe()
        subscription = null
    }

    override fun onClickButtonSetId(userId: String) {

        if (isLoading) return

        isLoading = true
        view.showProgress()

        subscription?.add(confirmExistingUserIdUsecase.confirm(userId)
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
                }))
    }

    private fun onConfirmExistingUserIdSuccess(isValid: Boolean) {
        if (isValid) {
            view.navigateToMain()
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
