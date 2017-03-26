package me.rei_m.hbfavmaterial.usecase

import me.rei_m.hbfavmaterial.domain.entity.EntryEntity
import me.rei_m.hbfavmaterial.constant.EntryTypeFilter
import rx.Observable

interface GetNewEntriesUsecase {
    fun get(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>>
}
