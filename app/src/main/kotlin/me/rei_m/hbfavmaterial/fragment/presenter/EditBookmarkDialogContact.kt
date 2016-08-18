package me.rei_m.hbfavmaterial.fragment.presenter

import me.rei_m.hbfavmaterial.entity.BookmarkEditEntity

interface EditBookmarkDialogContact {

    interface View {

        fun setSwitchOpenCheck(isChecked: Boolean)

        fun setSwitchShareTwitterCheck(isChecked: Boolean)

        fun setSwitchReadAfterCheck(isChecked: Boolean)

        fun setSwitchEnableByDelete(isEnabled: Boolean)

        fun showNetworkErrorMessage()

        fun showProgress()

        fun hideProgress()

        fun dismissDialog()

        fun startSettingActivity()
    }

    interface Actions {

        fun onCreate(view: EditBookmarkDialogContact.View,
                     bookmarkUrl: String,
                     bookmarkTitle: String,
                     bookmarkEditEntity: BookmarkEditEntity?)

        fun onViewCreated()

        fun onResume()

        fun onPause()

        fun onCheckedChangeOpen(isChecked: Boolean)

        fun onCheckedChangeShareTwitter(isChecked: Boolean)

        fun onCheckedChangeReadAfter(isChecked: Boolean)

        fun onCheckedChangeDelete(isChecked: Boolean)

        fun onClickButtonOk(isCheckedDelete: Boolean,
                            inputtedComment: String,
                            isCheckedOpen: Boolean,
                            isCheckedReadAfter: Boolean,
                            isCheckedShareTwitter: Boolean)
    }
}
