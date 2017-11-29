package me.rei_m.hbfavmaterial.viewmodel.widget.adapter

import android.databinding.ObservableField
import me.rei_m.hbfavmaterial.model.entity.BookmarkUserEntity
import javax.inject.Inject

class UserListItemViewModel @Inject constructor() {

    val bookmarkUser: ObservableField<BookmarkUserEntity> = ObservableField()

}
