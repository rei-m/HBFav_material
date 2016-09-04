package me.rei_m.hbfavmaterial.usecase.impl

import me.rei_m.hbfavmaterial.domain.entity.EntryEntity
import me.rei_m.hbfavmaterial.domain.repository.EntryRepository
import me.rei_m.hbfavmaterial.enum.EntryTypeFilter
import me.rei_m.hbfavmaterial.usecase.GetNewEntriesUsecase
import rx.Observable

class GetNewEntriesUsecaseImpl(private val entryRepository: EntryRepository) : GetNewEntriesUsecase {
    override fun get(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>> {
        return entryRepository.findNewByEntryType(entryTypeFilter)
    }
}
