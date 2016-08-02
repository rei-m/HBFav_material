package me.rei_m.hbfavmaterial.fragments.presenter

import rx.Subscription

interface EditBookmarkDialogContact {

    interface View {

        fun setSwitchShareTwitterCheck(isChecked: Boolean)

        fun showNetworkErrorMessage()

        fun showProgress()

        fun hideProgress()

        fun dismissDialog()

        fun startSettingActivity()
    }

    interface Actions {

        fun onViewCreated()

        fun changeCheckedShareTwitter(isChecked: Boolean)

        fun registerBookmark(url: String,
                             title: String,
                             comment: String,
                             isOpen: Boolean,
                             tags: List<String>,
                             isShareAtTwitter: Boolean): Subscription?

        fun deleteBookmark(bookmarkUrl: String): Subscription?
    }
}
