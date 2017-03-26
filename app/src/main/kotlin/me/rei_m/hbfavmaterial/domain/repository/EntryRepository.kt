package me.rei_m.hbfavmaterial.domain.repository

import io.reactivex.Single
import me.rei_m.hbfavmaterial.constant.EntryTypeFilter
import me.rei_m.hbfavmaterial.domain.entity.EntryEntity

interface EntryRepository {

    fun findHotByEntryType(entryTypeFilter: EntryTypeFilter): Single<List<EntryEntity>>

    fun findNewByEntryType(entryTypeFilter: EntryTypeFilter): Single<List<EntryEntity>>
}
