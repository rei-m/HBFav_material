package me.rei_m.hbfavmaterial.viewmodel.fragment

import android.databinding.ObservableField
import android.view.View
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.model.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.presentation.helper.Navigator

class BookmarkFragmentViewModel(private val navigator: Navigator) : AbsFragmentViewModel() {

    val bookmark: ObservableField<BookmarkEntity> = ObservableField()

    private var showArticleEventSubject = PublishSubject.create<String>()
    val showArticleEvent: Observable<String> = showArticleEventSubject

    fun onCreate(bookmark: BookmarkEntity) {
        this.bookmark.set(bookmark)
    }

    fun onClickHeader(view: View) {
        navigator.navigateToOthersBookmark(bookmark.get().creator)
    }

    fun onClickBody(view: View) {
        showArticleEventSubject.onNext(bookmark.get().article.url)
    }

    fun onClickBookmarkCount(view: View) {
        navigator.navigateToBookmarkedUsers(bookmark.get())
    }
}
