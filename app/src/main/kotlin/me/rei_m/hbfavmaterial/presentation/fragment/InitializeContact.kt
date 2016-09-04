package me.rei_m.hbfavmaterial.presentation.fragment

interface InitializeContact {

    interface View {

        fun showNetworkErrorMessage()

        fun showProgress()

        fun hideProgress()

        fun displayInvalidUserIdMessage()

        fun navigateToMain()
    }

    interface Actions {

        fun onCreate(view: View)

        fun onResume()

        fun onPause()

        fun onClickButtonSetId(userId: String)
    }
}
