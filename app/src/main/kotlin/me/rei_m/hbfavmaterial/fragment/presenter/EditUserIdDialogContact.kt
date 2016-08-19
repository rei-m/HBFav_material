package me.rei_m.hbfavmaterial.fragment.presenter

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

        fun onCreate(view: EditUserIdDialogContact.View)

        fun onViewCreated()

        fun onResume()

        fun onPause()

        fun onClickButtonOk(userId: String)
    }
}
