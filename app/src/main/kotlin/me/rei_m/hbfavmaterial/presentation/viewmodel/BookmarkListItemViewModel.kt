package me.rei_m.hbfavmaterial.presentation.viewmodel

import android.databinding.ObservableField
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import javax.inject.Inject

class BookmarkListItemViewModel @Inject constructor() {

    val bookmark: ObservableField<BookmarkEntity> = ObservableField()

}
