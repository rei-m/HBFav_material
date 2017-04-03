package me.rei_m.hbfavmaterial.presentation.viewmodel

import android.databinding.ObservableField
import me.rei_m.hbfavmaterial.domain.entity.EntryEntity
import javax.inject.Inject

class EntryListItemViewModel @Inject constructor() {
    val entry: ObservableField<EntryEntity> = ObservableField()
}
