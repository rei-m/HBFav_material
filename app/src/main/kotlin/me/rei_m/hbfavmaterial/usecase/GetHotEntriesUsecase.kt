package me.rei_m.hbfavmaterial.usecase

import io.reactivex.Single
import me.rei_m.hbfavmaterial.constant.EntryTypeFilter
import me.rei_m.hbfavmaterial.domain.entity.EntryEntity

interface GetHotEntriesUsecase {
    fun get(entryTypeFilter: EntryTypeFilter): Single<List<EntryEntity>>
}
