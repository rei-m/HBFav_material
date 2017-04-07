package me.rei_m.hbfavmaterial.viewmodel.widget

import android.databinding.ObservableField
import me.rei_m.hbfavmaterial.model.entity.BookmarkEntity
import javax.inject.Inject

class BookmarkListItemViewModel @Inject constructor() {

    val bookmark: ObservableField<BookmarkEntity> = ObservableField()

}
