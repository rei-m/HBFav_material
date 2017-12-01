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
import android.databinding.ObservableField
import android.view.View
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.model.entity.Bookmark

class BookmarkFragmentViewModel(bookmark: Bookmark) : ViewModel() {

    val bookmark: ObservableField<Bookmark> = ObservableField()

    private var onClickHeaderEventSubject = PublishSubject.create<String>()
    val onClickHeaderEvent: Observable<String> = onClickHeaderEventSubject

    private var onClickBookmarkCountEventSubject = PublishSubject.create<Bookmark>()
    val onClickBookmarkCountEvent: Observable<Bookmark> = onClickBookmarkCountEventSubject

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

    class Factory(private val bookmark: Bookmark) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BookmarkFragmentViewModel::class.java)) {
                return BookmarkFragmentViewModel(bookmark) as T
            }
            throw IllegalArgumentException("Unknown class name")
        }
    }
}
