package me.rei_m.hbfavmaterial.usecase

import me.rei_m.hbfavmaterial.domain.entity.EntryEntity
import me.rei_m.hbfavmaterial.enum.EntryTypeFilter
import rx.Observable

interface GetHotEntriesUsecase {
    fun get(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>>
}
