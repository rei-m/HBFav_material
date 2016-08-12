package me.rei_m.hbfavmaterial.fragment.presenter

import android.content.Context
import me.rei_m.hbfavmaterial.di.FragmentComponent
import me.rei_m.hbfavmaterial.entity.UserEntity
import me.rei_m.hbfavmaterial.repository.UserRepository
import me.rei_m.hbfavmaterial.service.UserService
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.net.HttpURLConnection
import javax.inject.Inject

class InitializePresenter(private val context: Context) : InitializeContact.Actions {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var userService: UserService

    override lateinit var view: InitializeContact.View

    private var subscription: CompositeSubscription? = null

    private var isLoading = false

    override fun onCreate(component: FragmentComponent,
                          view: InitializeContact.View) {
        component.inject(this)
        this.view = view

        val userEntity = userRepository.resolve()
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

        subscription?.add(userService.confirmExistingUserId(userId)
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
                }))
    }

    private fun onConfirmExistingUserIdSuccess(isValid: Boolean, userId: String) {
        if (isValid) {
            userRepository.store(context, UserEntity(userId))
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
