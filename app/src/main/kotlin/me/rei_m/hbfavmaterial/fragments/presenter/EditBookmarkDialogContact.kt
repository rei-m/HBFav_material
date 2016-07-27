package me.rei_m.hbfavmaterial.fragments.presenter

import rx.Subscription

interface EditBookmarkDialogContact {

    interface View {

        fun showNetworkErrorMessage()

        fun showProgress()

        fun hideProgress()

        fun dismiss()
    }

    interface Actions {

        fun changeCheckedShareTwitter(isChecked: Boolean)

        fun changeCheckedDelete(isChecked: Boolean)

        fun registerBookmark(url: String,
                             title: String,
                             comment: String,
                             isOpen: Boolean,
                             tags: List<String>,
                             isShareAtTwitter: Boolean): Subscription?

        fun deleteBookmark(bookmarkUrl: String): Subscription?
    }
}
