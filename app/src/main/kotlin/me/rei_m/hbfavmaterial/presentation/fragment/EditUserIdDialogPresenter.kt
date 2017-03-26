package me.rei_m.hbfavmaterial.presentation.fragment

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.usecase.ConfirmExistingUserIdUsecase
import me.rei_m.hbfavmaterial.usecase.GetUserUsecase
import retrofit2.HttpException
import java.net.HttpURLConnection

class EditUserIdDialogPresenter(private val getUserUsecase: GetUserUsecase,
                                private val confirmExistingUserIdUsecase: ConfirmExistingUserIdUsecase) : EditUserIdDialogContact.Actions {

    private lateinit var view: EditUserIdDialogContact.View

    private var disposable: CompositeDisposable? = null

    private var isLoading = false

    override fun onCreate(view: EditUserIdDialogContact.View) {
        this.view = view
    }

    override fun onViewCreated() {
        view.setEditUserId(getUserUsecase.get().id)
    }

    override fun onResume() {
        disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable?.dispose()
        disposable = null
    }

    override fun onClickButtonOk(userId: String) {

        val userEntity = getUserUsecase.get()

        if (!userEntity.isSameId(userId)) {
            disposable?.add(confirmExistingUserId(userId))
        } else {
            view.dismissDialog()
        }
    }

    private fun confirmExistingUserId(userId: String): Disposable? {

        if (isLoading) return null

        isLoading = true
        view.showProgress()

        return confirmExistingUserIdUsecase.confirm(userId)
                .subscribeAsync({
                    onConfirmExistingUserIdSuccess(it)
                }, {
                    onConfirmExistingUserIdFailure(it)
                }, {
                    isLoading = false
                    view.hideProgress()
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
