package me.rei_m.hbfavmaterial.service

import me.rei_m.hbfavmaterial.entities.EntryEntity
import me.rei_m.hbfavmaterial.enums.EntryTypeFilter
import rx.Observable

interface EntryService {
    fun findHotEntryByType(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>>

    fun findNewEntryByType(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>>
}
