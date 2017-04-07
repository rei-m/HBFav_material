package me.rei_m.hbfavmaterial.viewmodel.fragment

import android.databinding.ObservableField
import android.view.View
import me.rei_m.hbfavmaterial.model.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.event.ShowArticleEvent
import me.rei_m.hbfavmaterial.presentation.helper.Navigator

class BookmarkFragmentViewModel(private val rxBus: RxBus,
                                private val navigator: Navigator) : AbsFragmentViewModel() {

    val bookmark: ObservableField<BookmarkEntity> = ObservableField()

    fun onClickHeader(view: View) {
        navigator.navigateToOthersBookmark(bookmark.get().creator)
    }

    fun onClickBody(view: View) {
        rxBus.send(ShowArticleEvent(bookmark.get().article.url))
    }

    fun onClickBookmarkCount(view: View) {
        navigator.navigateToBookmarkedUsers(bookmark.get())
    }
}
