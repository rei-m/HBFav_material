package me.rei_m.hbfavmaterial.presentation.viewmodel

import android.databinding.ObservableField
import android.view.View
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.presentation.event.ShowArticleEvent
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator

class BookmarkFragmentViewModel(private val rxBus: RxBus,
                                private val navigator: ActivityNavigator) : AbsFragmentViewModel() {

    val bookmark: ObservableField<BookmarkEntity> = ObservableField()

    fun onClickHeader(view: View) {
        navigator.navigateToOthersBookmark(bookmark.get().creator)
    }

    fun onClickBody(view: View) {
        rxBus.send(ShowArticleEvent(bookmark.get().articleEntity.url))
    }

    fun onClickBookmarkCount(view: View) {
        navigator.navigateToBookmarkedUsers(bookmark.get())
    }
}
