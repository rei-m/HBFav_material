package me.rei_m.hbfavmaterial.fragments.presenter

import rx.Subscription

interface EditUserIdDialogContact {

    interface View {

        fun setEditUserId(userId: String)

        fun showNetworkErrorMessage()

        fun showProgress()

        fun hideProgress()

        fun dismissDialog()

        fun displayInvalidUserIdMessage()
    }

    interface Actions {

        fun onViewCreated()

        fun clickButtonOk(userId: String): Subscription?
    }
}
