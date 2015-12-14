package me.rei_m.hbfavmaterial.models

import me.rei_m.hbfavmaterial.entities.EntryEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.HotEntryLoadedEvent
import me.rei_m.hbfavmaterial.events.LoadedEventStatus
import me.rei_m.hbfavmaterial.network.HotEntryRss
import me.rei_m.hbfavmaterial.utils.BookmarkUtil.Companion.EntryType
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

public class HotEntryModel {

    public var isBusy = false
        private set

    public val entryList = ArrayList<EntryEntity>()

    public var entryType = EntryType.ALL
        private set

    public fun fetch(entryType: EntryType) {

        if (isBusy) {
            return
        }

        isBusy = true

        val listFromRss = ArrayList<EntryEntity>()

        val observer = object : Observer<EntryEntity> {
            override fun onNext(t: EntryEntity?) {
                listFromRss.add(t!!)
            }

            override fun onCompleted() {
                entryList.clear()
                entryList.addAll(listFromRss)
                this@HotEntryModel.entryType = entryType
                EventBusHolder.EVENT_BUS.post(HotEntryLoadedEvent(LoadedEventStatus.OK))
            }

            override fun onError(e: Throwable?) {
                EventBusHolder.EVENT_BUS.post(HotEntryLoadedEvent(LoadedEventStatus.ERROR))
            }
        }

        HotEntryRss().request(entryType)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo({
                    isBusy = false
                })
                .subscribe(observer)
    }
}