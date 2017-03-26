package me.rei_m.hbfavmaterial.usecase.impl

import io.reactivex.Single
import me.rei_m.hbfavmaterial.constant.EntryTypeFilter
import me.rei_m.hbfavmaterial.domain.entity.EntryEntity
import me.rei_m.hbfavmaterial.domain.repository.EntryRepository
import me.rei_m.hbfavmaterial.usecase.GetNewEntriesUsecase

class GetNewEntriesUsecaseImpl(private val entryRepository: EntryRepository) : GetNewEntriesUsecase {
    override fun get(entryTypeFilter: EntryTypeFilter): Single<List<EntryEntity>> {
        return entryRepository.findNewByEntryType(entryTypeFilter)
    }
}
