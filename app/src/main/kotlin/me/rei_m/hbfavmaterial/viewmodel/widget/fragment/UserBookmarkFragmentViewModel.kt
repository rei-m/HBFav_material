package me.rei_m.hbfavmaterial.viewmodel.widget.fragment

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.Observable
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.constant.ReadAfterFilter
import me.rei_m.hbfavmaterial.model.UserBookmarkModel
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.model.entity.BookmarkEntity

class UserBookmarkFragmentViewModel(private val userBookmarkModel: UserBookmarkModel,
                                    userModel: UserModel) : ViewModel() {

    val bookmarkList: ObservableArrayList<BookmarkEntity> = ObservableArrayList()

    val hasNextPage: ObservableBoolean = ObservableBoolean(false)

    val isVisibleEmpty: ObservableBoolean = ObservableBoolean(false)

    val isVisibleProgress: ObservableBoolean = ObservableBoolean(false)

    val isRefreshing: ObservableBoolean = ObservableBoolean(false)

    val readAfterFilter: ObservableField<ReadAfterFilter> = ObservableField(ReadAfterFilter.ALL)

    val isVisibleError: ObservableBoolean = ObservableBoolean(false)

    private var hasNextPageUpdatedEventSubject = BehaviorSubject.create<Boolean>()
    val hasNextPageUpdatedEvent: io.reactivex.Observable<Boolean> = hasNextPageUpdatedEventSubject

    private val onItemClickEventSubject = PublishSubject.create<BookmarkEntity>()
    val onItemClickEvent: io.reactivex.Observable<BookmarkEntity> = onItemClickEventSubject

    val onRaiseGetNextPageErrorEvent = userBookmarkModel.isRaisedGetNextPageError

    val onRaiseRefreshErrorEvent = userBookmarkModel.isRaisedRefreshError

    private val disposable: CompositeDisposable = CompositeDisposable()

    private val userId: ObservableField<String> = ObservableField("")

    private val userIdChangedCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            userBookmarkModel.getList(userId.get(), readAfterFilter.get())
        }
    }

    private val hasNextPageChangedCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            hasNextPageUpdatedEventSubject.onNext(hasNextPage.get())
        }
    }

    private val readAfterFilterChangedCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            userBookmarkModel.getList(userId.get(), readAfterFilter.get())
        }
    }

    init {
        userId.addOnPropertyChangedCallback(userIdChangedCallback)
        hasNextPage.addOnPropertyChangedCallback(hasNextPageChangedCallback)
        readAfterFilter.addOnPropertyChangedCallback(readAfterFilterChangedCallback)

        disposable.addAll(userBookmarkModel.bookmarkList.subscribe {
            if (it.isEmpty()) {
                bookmarkList.clear()
            } else {
                bookmarkList.addAll(it - bookmarkList)
            }
            isVisibleEmpty.set(bookmarkList.isEmpty())
        }, userBookmarkModel.hasNextPage.subscribe {
            hasNextPage.set(it)
        }, userBookmarkModel.isLoading.subscribe {
            isVisibleProgress.set(it)
        }, userBookmarkModel.isRefreshing.subscribe {
            isRefreshing.set(it)
        }, userBookmarkModel.isRaisedError.subscribe {
            isVisibleError.set(it)
        }, userModel.user.subscribe {
            userId.set(it.id)
        })
    }

    override fun onCleared() {
        userId.removeOnPropertyChangedCallback(userIdChangedCallback)
        hasNextPage.removeOnPropertyChangedCallback(hasNextPageChangedCallback)
        readAfterFilter.removeOnPropertyChangedCallback(readAfterFilterChangedCallback)
        disposable.dispose()
        super.onCleared()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        onItemClickEventSubject.onNext(bookmarkList[position])
    }

    @Suppress("UNUSED_PARAMETER")
    fun onScroll(listView: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        if (0 < totalItemCount && totalItemCount == firstVisibleItem + visibleItemCount) {
            userBookmarkModel.getNextPage(userId.get())
        }
    }

    fun onRefresh() {
        userBookmarkModel.refreshList(userId.get())
    }

    fun onOptionItemSelected(readAfterFilter: ReadAfterFilter) {
        this.readAfterFilter.set(readAfterFilter)
    }

    class Factory(private val userBookmarkModel: UserBookmarkModel,
                  private val userModel: UserModel) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserBookmarkFragmentViewModel::class.java)) {
                return UserBookmarkFragmentViewModel(userBookmarkModel, userModel) as T
            }
            throw IllegalArgumentException("Unknown class name")
        }
    }
}
