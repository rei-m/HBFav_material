package me.rei_m.hbfavmaterial.presentation.viewmodel

import android.databinding.ObservableField
import me.rei_m.hbfavmaterial.domain.entity.BookmarkUserEntity
import javax.inject.Inject

class UserListItemViewModel @Inject constructor() {

    val bookmarkUser: ObservableField<BookmarkUserEntity> = ObservableField()

}
