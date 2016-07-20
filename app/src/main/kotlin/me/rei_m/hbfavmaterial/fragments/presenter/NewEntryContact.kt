package me.rei_m.hbfavmaterial.fragments.presenter

import me.rei_m.hbfavmaterial.entities.EntryEntity
import me.rei_m.hbfavmaterial.enums.EntryTypeFilter
import rx.Subscription

interface NewEntryContact {

    interface View {

        fun showEntryList(entryList: List<EntryEntity>)

        fun hideEntryList()

        fun showNetworkErrorMessage()

        fun showProgress()

        fun hideProgress()

        fun showEmpty()

        fun hideEmpty()
    }

    interface Actions {

        fun clickEntry(entryEntity: EntryEntity)

        fun initializeListContents(): Subscription?

        fun fetchListContents(): Subscription?

        fun toggleListContents(entryTypeFilter: EntryTypeFilter): Subscription?
    }
}
