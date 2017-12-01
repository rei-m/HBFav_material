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
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.constant.ReadAfterFilter
import me.rei_m.hbfavmaterial.model.UserBookmarkModel
import me.rei_m.hbfavmaterial.model.entity.Bookmark

class OthersBookmarkFragmentViewModel(private val userBookmarkModel: UserBookmarkModel,
                                      private val bookmarkUserId: String) : ViewModel() {

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

    val onRaiseGetNextPageErrorEvent = userBookmarkModel.isRaisedGetNextPageError

    val onRaiseRefreshErrorEvent = userBookmarkModel.isRaisedRefreshError

    private val disposable: CompositeDisposable = CompositeDisposable()

    private val hasNextPageChangedCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            hasNextPageUpdatedEventSubject.onNext(hasNextPage.get())
        }
    }

    init {
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
        })

        hasNextPage.addOnPropertyChangedCallback(hasNextPageChangedCallback)

        userBookmarkModel.getList(bookmarkUserId, ReadAfterFilter.ALL)
    }

    override fun onCleared() {
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
            userBookmarkModel.getNextPage(bookmarkUserId)
        }
    }

    fun onRefresh() {
        userBookmarkModel.refreshList(bookmarkUserId)
    }

    class Factory(private val userBookmarkModel: UserBookmarkModel,
                  private val userId: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OthersBookmarkFragmentViewModel::class.java)) {
                return OthersBookmarkFragmentViewModel(userBookmarkModel, userId) as T
            }
            throw IllegalArgumentException("Unknown class name")
        }
    }
}
