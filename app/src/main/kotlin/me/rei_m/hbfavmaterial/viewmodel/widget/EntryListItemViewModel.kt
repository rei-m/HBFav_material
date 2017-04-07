package me.rei_m.hbfavmaterial.viewmodel.widget

import android.databinding.ObservableField
import me.rei_m.hbfavmaterial.model.entity.EntryEntity
import javax.inject.Inject

class EntryListItemViewModel @Inject constructor() {
    val entry: ObservableField<EntryEntity> = ObservableField()
}
