package me.rei_m.hbfavmaterial.fragment.presenter

import android.content.Context
import android.support.v4.app.DialogFragment
import me.rei_m.hbfavmaterial.entitiy.UserEntity
import me.rei_m.hbfavmaterial.extension.getAppContext
import me.rei_m.hbfavmaterial.repository.UserRepository
import me.rei_m.hbfavmaterial.service.UserService
import retrofit2.adapter.rxjava.HttpException
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.HttpURLConnection
import javax.inject.Inject

class EditUserIdDialogPresenter(private val view: EditUserIdDialogContact.View) : EditUserIdDialogContact.Actions {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var userService: UserService

    private val appContext: Context
        get() = (view as DialogFragment).getAppContext()

    private var isLoading = false

    override fun onViewCreated() {
        view.setEditUserId(userRepository.resolve().id)
    }

    override fun clickButtonOk(userId: String): Subscription? {

        val userEntity = userRepository.resolve()

        if (!userEntity.isSameId(userId)) {
            return confirmExistingUserId(userId)
        } else {
            view.dismissDialog()
            return null
        }
    }

    private fun confirmExistingUserId(userId: String): Subscription? {

        if (isLoading) return null

        isLoading = true
        view.showProgress()

        return userService.confirmExistingUserId(userId)
                .doOnUnsubscribe {
                    isLoading = false
                    view.hideProgress()
                }
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onConfirmExistingUserIdSuccess(it, userId)
                }, {
                    onConfirmExistingUserIdFailure(it)
                })
    }

    private fun onConfirmExistingUserIdSuccess(isValid: Boolean, userId: String) {
        if (isValid) {
            userRepository.store(appContext, UserEntity(userId))
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
