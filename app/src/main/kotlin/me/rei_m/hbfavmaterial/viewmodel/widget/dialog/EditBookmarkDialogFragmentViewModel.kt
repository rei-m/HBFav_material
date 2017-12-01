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

package me.rei_m.hbfavmaterial.viewmodel.widget.dialog

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.application.TwitterService
import me.rei_m.hbfavmaterial.model.entity.EditableBookmark
import io.reactivex.Observable as RxObservable

class EditBookmarkDialogFragmentViewModel(articleTitle: String,
                                          articleUrl: String,
                                          private val hatenaService: HatenaService,
                                          private val twitterService: TwitterService) : ViewModel() {

    companion object {
        private const val MAX_COMMENT_SIZE = 100
    }

    val isFirstEdit: ObservableBoolean = ObservableBoolean(true)

    val articleTitle: ObservableField<String> = ObservableField("")

    val articleUrl: ObservableField<String> = ObservableField("")

    val comment: ObservableField<String> = ObservableField("")

    val commentCount: ObservableField<String> = ObservableField("0 / $MAX_COMMENT_SIZE")

    val isEnableComment: ObservableBoolean = ObservableBoolean(true)

    val isOpen: ObservableBoolean = ObservableBoolean(true)

    val isShareTwitter: ObservableBoolean = ObservableBoolean(false)

    val isReadAfter: ObservableBoolean = ObservableBoolean(false)

    var isDelete: ObservableBoolean = ObservableBoolean(false)

    val hatenaUnauthorizedEvent = hatenaService.unauthorizedEvent

    private var twitterUnauthorizedEventSubject = PublishSubject.create<Unit>()
    val twitterUnauthorizedEvent: RxObservable<Unit> = twitterUnauthorizedEventSubject

    val isLoading = hatenaService.isLoading

    val raisedErrorEvent = hatenaService.raisedErrorEvent

    private var dismissDialogEventSubject = PublishSubject.create<Unit>()
    val dismissDialogEvent: RxObservable<Unit> = dismissDialogEventSubject

    private var isAuthorizedTwitter: Boolean = false

    private var tags: List<String> = listOf()

    private val disposable = CompositeDisposable()

    private val commentChangedCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            val size = comment.get().codePointCount(0, comment.get().length)
            commentCount.set("$size / $MAX_COMMENT_SIZE")
            isEnableComment.set(size <= MAX_COMMENT_SIZE)
        }
    }

    private val isShareTwitterCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(p0: Observable?, p1: Int) {
            if (isShareTwitter.get()) {
                if (!isAuthorizedTwitter) {
                    twitterUnauthorizedEventSubject.onNext(Unit)
                }
            }
        }
    }

    init {
        val displayItemStream = RxObservable.zip<EditableBookmark, Boolean, Pair<EditableBookmark, Boolean>>(
                hatenaService.editableBookmark,
                twitterService.confirmAuthorisedEvent,
                BiFunction { t1, t2 ->
                    return@BiFunction Pair(t1, t2)
                }
        )

        comment.addOnPropertyChangedCallback(commentChangedCallback)
        isShareTwitter.addOnPropertyChangedCallback(isShareTwitterCallback)

        disposable.addAll(displayItemStream.subscribe { (editableBookmark, isAuthorizedTwitter) ->
            isFirstEdit.set(editableBookmark.isFirstEdit)
            comment.set(editableBookmark.comment)
            isOpen.set(!editableBookmark.isPrivate)
            isReadAfter.set(editableBookmark.tags.contains(HatenaService.TAG_READ_AFTER))
            this.tags = editableBookmark.tags
            this.isAuthorizedTwitter = isAuthorizedTwitter
        }, hatenaService.registeredEvent.subscribe {
            if (isShareTwitter.get()) {
                twitterService.postTweet(this.articleUrl.get(), this.articleTitle.get(), comment.get())
            }
            dismissDialogEventSubject.onNext(Unit)
        }, hatenaService.deletedEvent.subscribe {
            dismissDialogEventSubject.onNext(Unit)
        })

        this.articleTitle.set(articleTitle)
        this.articleUrl.set(articleUrl)

        hatenaService.findBookmarkByUrl(articleUrl)
        twitterService.confirmAuthorised()
    }

    override fun onCleared() {
        comment.removeOnPropertyChangedCallback(commentChangedCallback)
        isShareTwitter.removeOnPropertyChangedCallback(isShareTwitterCallback)
        disposable.dispose()
        super.onCleared()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickOk(view: View) {
        if (isDelete.get()) {
            hatenaService.deleteBookmark(articleUrl.get())
        } else {
            hatenaService.registerBookmark(articleUrl.get(),
                    comment.get(),
                    isOpen.get(),
                    isReadAfter.get(),
                    tags)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickCancel(view: View) {
        dismissDialogEventSubject.onNext(Unit)
    }

    class Factory(private val articleTitle: String,
                  private val articleUrl: String,
                  private val hatenaService: HatenaService,
                  private val twitterService: TwitterService) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EditBookmarkDialogFragmentViewModel::class.java)) {
                return EditBookmarkDialogFragmentViewModel(articleTitle, articleUrl, hatenaService, twitterService) as T
            }
            throw IllegalArgumentException("Unknown class name")
        }
    }
}
