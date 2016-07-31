package me.rei_m.hbfavmaterial.fragments.presenter

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
        fun clickButtonSetId(userId: String): Subscription?
    }
}
