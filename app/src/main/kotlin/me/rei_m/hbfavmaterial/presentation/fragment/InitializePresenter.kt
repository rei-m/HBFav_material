package me.rei_m.hbfavmaterial.presentation.fragment

import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.usecase.ConfirmExistingUserIdUsecase
import me.rei_m.hbfavmaterial.usecase.GetUserUsecase
import retrofit2.HttpException
import java.net.HttpURLConnection

class InitializePresenter(private val getUserUsecase: GetUserUsecase,
                          private val confirmExistingUserIdUsecase: ConfirmExistingUserIdUsecase) : InitializeContact.Actions {

    private lateinit var view: InitializeContact.View

    private var disposable: CompositeDisposable? = null

    private var isLoading = false

    override fun onCreate(view: InitializeContact.View) {

        this.view = view

        val userEntity = getUserUsecase.get()
        if (userEntity.isCompleteSetting) {
            view.navigateToMain()
        }
    }

    override fun onResume() {
        disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable?.dispose()
        disposable = null
    }

    override fun onClickButtonSetId(userId: String) {

        if (isLoading) return

        isLoading = true
        view.showProgress()

        disposable?.add(confirmExistingUserIdUsecase.confirm(userId).subscribeAsync({
            onConfirmExistingUserIdSuccess(it)
        }, {
            onConfirmExistingUserIdFailure(it)
        }, {
            isLoading = false
            view.hideProgress()
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
