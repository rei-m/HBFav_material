package me.rei_m.hbfavmaterial.fragment.presenter

import me.rei_m.hbfavmaterial.entity.EntryEntity
import me.rei_m.hbfavmaterial.enum.EntryTypeFilter

interface HotEntryContact {

    interface View {

        fun showEntryList(entryList: List<EntryEntity>)

        fun hideEntryList()

        fun showNetworkErrorMessage()

        fun showProgress()

        fun hideProgress()

        fun showEmpty()

        fun hideEmpty()

        fun navigateToBookmark(entryEntity: EntryEntity)
    }

    interface Actions {

        var entryTypeFilter: EntryTypeFilter

        fun onCreate(view: HotEntryContact.View,
                     entryTypeFilter: EntryTypeFilter)

        fun onResume()

        fun onPause()

        fun onRefreshList()

        fun onOptionItemSelected(entryTypeFilter: EntryTypeFilter)

        fun onClickEntry(entryEntity: EntryEntity)
    }
}
