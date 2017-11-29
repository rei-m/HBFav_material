package me.rei_m.hbfavmaterial.viewmodel.widget.fragment

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.Observable
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.view.View
import android.widget.AdapterView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.constant.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.model.BookmarkModel
import me.rei_m.hbfavmaterial.model.entity.BookmarkUserEntity

class BookmarkedUsersFragmentViewModel(private val bookmarkModel: BookmarkModel,
                                       private val articleUrl: String = "") : ViewModel() {

    val bookmarkUserList: ObservableArrayList<BookmarkUserEntity> = ObservableArrayList()

    val isVisibleEmpty: ObservableBoolean = ObservableBoolean(false)

    val isVisibleProgress: ObservableBoolean = ObservableBoolean(false)

    val isRefreshing: ObservableBoolean = ObservableBoolean(false)

    val bookmarkCommentFilter: ObservableField<BookmarkCommentFilter> = ObservableField(BookmarkCommentFilter.ALL)

    val isVisibleError: ObservableBoolean = ObservableBoolean(false)

    private val onItemClickEventSubject = PublishSubject.create<String>()
    val onItemClickEvent: io.reactivex.Observable<String> = onItemClickEventSubject

    val onRaiseRefreshErrorEvent = bookmarkModel.isRaisedRefreshError

    private val disposable: CompositeDisposable = CompositeDisposable()

    private val bookmarkCommentFilterChangedCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            bookmarkModel.getUserList(articleUrl, bookmarkCommentFilter.get())
        }
    }

    init {
        disposable.addAll(bookmarkModel.bookmarkUserList.subscribe {
            if (it.isEmpty()) {
                bookmarkUserList.clear()
            } else {
                bookmarkUserList.addAll(it)
            }
            isVisibleEmpty.set(bookmarkUserList.isEmpty())
        }, bookmarkModel.isLoading.subscribe {
            isVisibleProgress.set(it)
        }, bookmarkModel.isRefreshing.subscribe {
            isRefreshing.set(it)
        }, bookmarkModel.isRaisedGetError.subscribe {
            isVisibleError.set(it)
        })

        bookmarkCommentFilter.addOnPropertyChangedCallback(bookmarkCommentFilterChangedCallback)

        bookmarkModel.getUserList(articleUrl, bookmarkCommentFilter.get())
    }

    override fun onCleared() {
        bookmarkCommentFilter.removeOnPropertyChangedCallback(bookmarkCommentFilterChangedCallback)
        disposable.dispose()
        super.onCleared()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        onItemClickEventSubject.onNext(bookmarkUserList[position].creator)
    }

    fun onRefresh() {
        bookmarkModel.refreshUserList(articleUrl, bookmarkCommentFilter.get())
    }

    class Factory(private val bookmarkModel: BookmarkModel,
                  private val articleUrl: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BookmarkedUsersFragmentViewModel::class.java)) {
                return BookmarkedUsersFragmentViewModel(bookmarkModel, articleUrl) as T
            }
            throw IllegalArgumentException("Unknown class name")
        }
    }
}
