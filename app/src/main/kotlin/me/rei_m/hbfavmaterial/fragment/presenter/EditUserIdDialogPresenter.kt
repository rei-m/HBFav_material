package me.rei_m.hbfavmaterial.fragment.presenter

import android.content.Context
import me.rei_m.hbfavmaterial.di.ActivityComponent
import me.rei_m.hbfavmaterial.entitiy.UserEntity
import me.rei_m.hbfavmaterial.repository.UserRepository
import me.rei_m.hbfavmaterial.service.UserService
import retrofit2.adapter.rxjava.HttpException
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.net.HttpURLConnection
import javax.inject.Inject

class EditUserIdDialogPresenter(private val context: Context) : EditUserIdDialogContact.Actions {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var userService: UserService

    private lateinit var view: EditUserIdDialogContact.View

    private var subscription: CompositeSubscription? = null

    private var isLoading = false

    override fun onCreate(component: ActivityComponent,
                          view: EditUserIdDialogContact.View) {
        component.inject(this)
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
            userRepository.store(context, UserEntity(userId))
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
