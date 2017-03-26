package me.rei_m.hbfavmaterial.usecase

import io.reactivex.Single
import me.rei_m.hbfavmaterial.constant.EntryTypeFilter
import me.rei_m.hbfavmaterial.domain.entity.EntryEntity

interface GetNewEntriesUsecase {
    fun get(entryTypeFilter: EntryTypeFilter): Single<List<EntryEntity>>
}
