package me.rei_m.hbfavmaterial.domain.repository

import me.rei_m.hbfavmaterial.domain.entity.EntryEntity
import me.rei_m.hbfavmaterial.constant.EntryTypeFilter
import rx.Observable

interface EntryRepository {

    fun findHotByEntryType(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>>

    fun findNewByEntryType(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>>
}
