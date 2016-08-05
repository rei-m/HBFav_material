package me.rei_m.hbfavmaterial.service

import me.rei_m.hbfavmaterial.entitiy.EntryEntity
import me.rei_m.hbfavmaterial.enum.EntryTypeFilter
import rx.Observable

interface EntryService {
    fun findHotEntryByType(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>>

    fun findNewEntryByType(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>>
}
