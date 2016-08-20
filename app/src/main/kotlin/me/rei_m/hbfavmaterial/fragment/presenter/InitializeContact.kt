package me.rei_m.hbfavmaterial.fragment.presenter


interface InitializeContact {

    interface View {

        fun showNetworkErrorMessage()

        fun showProgress()

        fun hideProgress()

        fun displayInvalidUserIdMessage()

        fun navigateToMain()
    }

    interface Actions {

        fun onCreate(view: InitializeContact.View)

        fun onResume()

        fun onPause()

        fun onClickButtonSetId(userId: String)
    }
}
