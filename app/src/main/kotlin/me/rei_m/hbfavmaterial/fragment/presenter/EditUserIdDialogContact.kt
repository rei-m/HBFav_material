package me.rei_m.hbfavmaterial.fragment.presenter

import me.rei_m.hbfavmaterial.di.ActivityComponent

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

        fun onCreate(component: ActivityComponent,
                     view: EditUserIdDialogContact.View)

        fun onViewCreated()

        fun onResume()

        fun onPause()

        fun onClickButtonOk(userId: String)
    }
}
