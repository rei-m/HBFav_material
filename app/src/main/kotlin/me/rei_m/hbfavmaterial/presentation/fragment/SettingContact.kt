package me.rei_m.hbfavmaterial.presentation.fragment

import android.app.Activity
import android.content.Intent

interface SettingContact {

    interface View {

        fun setUserId(userId: String)

        fun updateUserId(userId: String)

        fun setHatenaAuthoriseStatus(isAuthorised: Boolean)

        fun setTwitterAuthoriseStatus(isAuthorised: Boolean)

        fun showNetworkErrorMessage()
    }

    interface Actions {

        fun onCreate(view: View)

        fun onViewCreated()

        fun onResume()

        fun onPause()

        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

        fun onDismissEditUserIdDialog()

        fun onClickTwitterAuthorize(activity: Activity)
    }
}
