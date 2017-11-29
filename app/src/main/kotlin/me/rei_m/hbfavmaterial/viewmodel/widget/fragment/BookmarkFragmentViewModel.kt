package me.rei_m.hbfavmaterial.viewmodel.widget.fragment

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import android.view.View
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.model.entity.BookmarkEntity

class BookmarkFragmentViewModel(bookmark: BookmarkEntity) : ViewModel() {

    val bookmark: ObservableField<BookmarkEntity> = ObservableField()

    private var onClickHeaderEventSubject = PublishSubject.create<String>()
    val onClickHeaderEvent: Observable<String> = onClickHeaderEventSubject

    private var onClickBookmarkCountEventSubject = PublishSubject.create<BookmarkEntity>()
    val onClickBookmarkCountEvent: Observable<BookmarkEntity> = onClickBookmarkCountEventSubject

    private var onClickBodyEventSubject = PublishSubject.create<String>()
    val onClickBodyEvent: Observable<String> = onClickBodyEventSubject

    init {
        this.bookmark.set(bookmark)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickHeader(view: View) {
        onClickHeaderEventSubject.onNext(bookmark.get().creator)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickBody(view: View) {
        onClickBodyEventSubject.onNext(bookmark.get().article.url)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickBookmarkCount(view: View) {
        onClickBookmarkCountEventSubject.onNext(bookmark.get())
    }

    class Factory(private val bookmark: BookmarkEntity) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BookmarkFragmentViewModel::class.java)) {
                return BookmarkFragmentViewModel(bookmark) as T
            }
            throw IllegalArgumentException("Unknown class name")
        }
    }
}
