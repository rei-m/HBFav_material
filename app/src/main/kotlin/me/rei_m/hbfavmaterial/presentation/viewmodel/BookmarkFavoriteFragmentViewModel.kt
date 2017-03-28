package me.rei_m.hbfavmaterial.presentation.viewmodel

import android.databinding.ObservableArrayList
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.usecase.GetFavoriteBookmarksUsecase


class BookmarkFavoriteFragmentViewModel(private val getFavoriteBookmarksUsecase: GetFavoriteBookmarksUsecase) : AbsFragmentViewModel() {

    private var disposable: CompositeDisposable? = null

    var bookmarkList: ObservableArrayList<BookmarkEntity> = ObservableArrayList()
        private set

    override fun onStart() {
    }

    override fun onResume() {
        disposable = CompositeDisposable()
        disposable?.add(getFavoriteBookmarksUsecase.get(0).subscribeAsync({
            bookmarkList.clear()
            bookmarkList.addAll(it)
        }, {
            // TODO: Errorの場合
        }, {
            // TODO: ローディング解除.
        }))
    }

    override fun onPause() {
        disposable?.dispose()
        disposable = null
    }

    override fun onStop() {
    }
}
