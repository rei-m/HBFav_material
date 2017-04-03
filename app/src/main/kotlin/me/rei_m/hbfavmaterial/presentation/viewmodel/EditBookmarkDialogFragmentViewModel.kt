package me.rei_m.hbfavmaterial.presentation.viewmodel

import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.view.View
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEditEntity
import me.rei_m.hbfavmaterial.domain.repository.TwitterSessionRepository
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.domain.service.HatenaService
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.presentation.event.*
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.usecase.DeleteBookmarkUsecase
import me.rei_m.hbfavmaterial.usecase.RegisterBookmarkUsecase
import retrofit2.HttpException
import java.net.HttpURLConnection


class EditBookmarkDialogFragmentViewModel(private val userRepository: UserRepository,
                                          private val twitterSessionRepository: TwitterSessionRepository,
                                          private val registerBookmarkUsecase: RegisterBookmarkUsecase,
                                          private val deleteBookmarkUsecase: DeleteBookmarkUsecase,
                                          private val rxBus: RxBus,
                                          private val navigator: ActivityNavigator) : AbsFragmentViewModel() {

    companion object {
        private const val MAX_COMMENT_SIZE = 100
    }

    val isFirstEdit: ObservableBoolean = ObservableBoolean(true)

    val articleTitle: ObservableField<String> = ObservableField("")

    val comment: ObservableField<String> = ObservableField("")

    val commentCount: ObservableField<String> = ObservableField("0 / $MAX_COMMENT_SIZE")

    val isEnableComment: ObservableBoolean = ObservableBoolean(true)

    val isOpen: ObservableBoolean = ObservableBoolean(true)

    val isShareTwitter: ObservableBoolean = ObservableBoolean(false)

    val isReadAfter: ObservableBoolean = ObservableBoolean(false)

    var isDelete: ObservableBoolean = ObservableBoolean(false)

    private lateinit var bookmarkTitle: String

    private lateinit var bookmarkEdit: BookmarkEditEntity

    private var isLoading: Boolean = false

    init {
        comment.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(p0: Observable?, p1: Int) {
                val size = comment.get().codePointCount(0, comment.get().length)
                commentCount.set("$size / $MAX_COMMENT_SIZE")
                isEnableComment.set(size <= MAX_COMMENT_SIZE)
            }
        })

        isShareTwitter.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(p0: Observable?, p1: Int) {
                if (isShareTwitter.get()) {
                    val twitterSession = twitterSessionRepository.resolve()
                    if (!twitterSession.oAuthTokenEntity.isAuthorised) {
                        navigator.navigateToSetting()
                        rxBus.send(DismissEditBookmarkDialogEvent())
                    }
                }
            }
        })
    }

    fun onCreate(bookmarkTitle: String, bookmarkEdit: BookmarkEditEntity) {
        isFirstEdit.set(bookmarkEdit.isFirstEdit)
        articleTitle.set(bookmarkTitle)
        comment.set(bookmarkEdit.comment)
        isOpen.set(!bookmarkEdit.isPrivate)
        isReadAfter.set(bookmarkEdit.tags.contains(HatenaService.TAG_READ_AFTER))

        val user = userRepository.resolve()
        isOpen.set(user.isCheckedPostBookmarkOpen)
        isReadAfter.set(user.isCheckedPostBookmarkReadAfter)

        val twitterSession = twitterSessionRepository.resolve()
        isShareTwitter.set(twitterSession.isShare)

        this.bookmarkTitle = bookmarkTitle
        this.bookmarkEdit = bookmarkEdit
    }

    fun onClickOk(view: View) {
        if (isDelete.get()) {
            deleteBookmark(bookmarkEdit.url)
        } else {
            registerBookmark(bookmarkEdit.url,
                    bookmarkTitle,
                    comment.get(),
                    isOpen.get(),
                    isReadAfter.get(),
                    isShareTwitter.get())
        }
    }

    private fun registerBookmark(url: String,
                                 title: String,
                                 comment: String,
                                 isOpen: Boolean,
                                 isCheckedReadAfter: Boolean,
                                 isShareAtTwitter: Boolean) {
        if (isLoading) return

        isLoading = true
        rxBus.send(ShowProgressDialogEvent())

        registerDisposable(registerBookmarkUsecase.register(url, title, comment, bookmarkEdit.tags, isOpen, isCheckedReadAfter, isShareAtTwitter).subscribeAsync({
            rxBus.send(DismissEditBookmarkDialogEvent())
        }, {
            rxBus.send(FailToConnectionEvent())
        }, {
            isLoading = false
            rxBus.send(DismissProgressDialogEvent())
        }))
    }

    private fun deleteBookmark(bookmarkUrl: String) {

        if (isLoading) return

        isLoading = true
        rxBus.send(ShowProgressDialogEvent())

        registerDisposable(deleteBookmarkUsecase.delete(bookmarkUrl).subscribeAsync({
            rxBus.send(DismissEditBookmarkDialogEvent())
        }, {
            if (it is HttpException) {
                if (it.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                    rxBus.send(DismissEditBookmarkDialogEvent())
                    return@subscribeAsync
                }
            }
            rxBus.send(FailToConnectionEvent())
        }, {
            isLoading = false
            rxBus.send(DismissProgressDialogEvent())
        }))
    }

    fun onClickCancel(view: View) {
        rxBus.send(DismissEditBookmarkDialogEvent())
    }
}
