package me.rei_m.hbfavmaterial.fragment.presenter

import rx.Subscription


interface InitializeContact {

    interface View {

        fun showNetworkErrorMessage()

        fun showProgress()

        fun hideProgress()

        fun displayInvalidUserIdMessage()

        fun navigateToMain()
    }

    interface Actions {

        fun onCreate()

        fun clickButtonSetId(userId: String): Subscription?
    }
}
