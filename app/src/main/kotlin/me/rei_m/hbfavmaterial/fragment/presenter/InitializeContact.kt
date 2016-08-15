package me.rei_m.hbfavmaterial.fragment.presenter

import me.rei_m.hbfavmaterial.di.FragmentComponent


interface InitializeContact {

    interface View {

        fun showNetworkErrorMessage()

        fun showProgress()

        fun hideProgress()

        fun displayInvalidUserIdMessage()

        fun navigateToMain()
    }

    interface Actions {

        var view: InitializeContact.View

        fun onCreate(component: FragmentComponent,
                     view: InitializeContact.View)

        fun onResume()

        fun onPause()

        fun onClickButtonSetId(userId: String)
    }
}
