/*
 * Copyright (c) 2017. Rei Matsushita
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

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
import me.rei_m.hbfavmaterial.model.FavoriteBookmarkModel
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.model.entity.Bookmark

class FavoriteBookmarkFragmentViewModel(private val favoriteBookmarkModel: FavoriteBookmarkModel,
                                        userModel: UserModel) : ViewModel() {

    val bookmarkList: ObservableArrayList<Bookmark> = ObservableArrayList()

    val hasNextPage: ObservableBoolean = ObservableBoolean(false)

    val isVisibleEmpty: ObservableBoolean = ObservableBoolean(false)

    val isVisibleProgress: ObservableBoolean = ObservableBoolean(false)

    val isRefreshing: ObservableBoolean = ObservableBoolean(false)

    val isVisibleError: ObservableBoolean = ObservableBoolean(false)

    private var hasNextPageUpdatedEventSubject = BehaviorSubject.create<Boolean>()
    val hasNextPageUpdatedEvent: io.reactivex.Observable<Boolean> = hasNextPageUpdatedEventSubject

    private val onItemClickEventSubject = PublishSubject.create<Bookmark>()
    val onItemClickEvent: io.reactivex.Observable<Bookmark> = onItemClickEventSubject

    val onRaiseGetNextPageErrorEvent = favoriteBookmarkModel.isRaisedGetNextPageError

    val onRaiseRefreshErrorEvent = favoriteBookmarkModel.isRaisedRefreshError

    private val disposable: CompositeDisposable = CompositeDisposable()

    private val userId: ObservableField<String> = ObservableField("")

    private val userIdChangedCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            favoriteBookmarkModel.getList(userId.get())
        }
    }

    private val hasNextPageChangedCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            hasNextPageUpdatedEventSubject.onNext(hasNextPage.get())
        }
    }

    init {
        userId.addOnPropertyChangedCallback(userIdChangedCallback)
        hasNextPage.addOnPropertyChangedCallback(hasNextPageChangedCallback)

        disposable.addAll(favoriteBookmarkModel.bookmarkList.subscribe {
            if (it.isEmpty()) {
                bookmarkList.clear()
            } else {
                bookmarkList.addAll(it - bookmarkList)
            }
            isVisibleEmpty.set(bookmarkList.isEmpty())
        }, favoriteBookmarkModel.hasNextPage.subscribe {
            hasNextPage.set(it)
        }, favoriteBookmarkModel.isLoading.subscribe {
            isVisibleProgress.set(it)
        }, favoriteBookmarkModel.isRefreshing.subscribe {
            isRefreshing.set(it)
        }, favoriteBookmarkModel.isRaisedError.subscribe {
            isVisibleError.set(it)
        }, userModel.user.subscribe {
            userId.set(it.id)
        })
    }

    override fun onCleared() {
        userId.removeOnPropertyChangedCallback(userIdChangedCallback)
        hasNextPage.removeOnPropertyChangedCallback(hasNextPageChangedCallback)
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
            favoriteBookmarkModel.getNextPage(userId.get())
        }
    }

    fun onRefresh() {
        favoriteBookmarkModel.refreshList(userId.get())
    }

    class Factory(private val favoriteBookmarkModel: FavoriteBookmarkModel,
                  private val userModel: UserModel) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavoriteBookmarkFragmentViewModel::class.java)) {
                return FavoriteBookmarkFragmentViewModel(favoriteBookmarkModel, userModel) as T
            }
            throw IllegalArgumentException("Unknown class name")
        }
    }
}
