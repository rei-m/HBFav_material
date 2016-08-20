package me.rei_m.hbfavmaterial.fragment.presenter

import me.rei_m.hbfavmaterial.entity.UserEntity
import me.rei_m.hbfavmaterial.repository.UserRepository
import me.rei_m.hbfavmaterial.service.UserService
import retrofit2.adapter.rxjava.HttpException
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.net.HttpURLConnection

class EditUserIdDialogPresenter(private val userRepository: UserRepository,
                                private val userService: UserService) : EditUserIdDialogContact.Actions {

    private lateinit var view: EditUserIdDialogContact.View

    private var subscription: CompositeSubscription? = null

    private var isLoading = false

    override fun onCreate(view: EditUserIdDialogContact.View) {
        this.view = view
    }

    override fun onViewCreated() {
        view.setEditUserId(userRepository.resolve().id)
    }

    override fun onResume() {
        subscription = CompositeSubscription()
    }

    override fun onPause() {
        subscription?.unsubscribe()
        subscription = null
    }

    override fun onClickButtonOk(userId: String) {

        val userEntity = userRepository.resolve()

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
            userRepository.store(UserEntity(userId))
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
