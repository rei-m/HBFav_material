package me.rei_m.hbfavmaterial.viewmodel.fragment

import android.app.ProgressDialog
import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.view.View
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.application.TwitterService
import me.rei_m.hbfavmaterial.model.entity.EditableBookmarkEntity
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory

class EditBookmarkDialogFragmentViewModel(private val hatenaService: HatenaService,
                                          private val twitterService: TwitterService,
                                          private val navigator: Navigator,
                                          private val progressDialog: ProgressDialog) : AbsFragmentViewModel() {

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

    private var dismissDialogEventSubject = PublishSubject.create<Unit>()
    val dismissDialogEvent: io.reactivex.Observable<Unit> = dismissDialogEventSubject

    lateinit var articleUrl: String

    private var snackbarFactory: SnackbarFactory? = null

    private var isAuthorizedTwitter: Boolean = false

    private var tags: List<String> = listOf()

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
                    if (!isAuthorizedTwitter) {
                        navigator.navigateToSetting()
                        dismissDialogEventSubject.onNext(Unit)
                    }
                }
            }
        })
    }

    fun onCreate(articleTitle: String, articleUrl: String) {
        this.articleTitle.set(articleTitle)
        this.articleUrl = articleUrl
    }

    fun onCreateView(snackbarFactory: SnackbarFactory) {
        this.snackbarFactory = snackbarFactory
    }

    override fun onStart() {
        super.onStart()
        registerDisposable(io.reactivex.Observable.zip<EditableBookmarkEntity, Boolean, Pair<EditableBookmarkEntity, Boolean>>(hatenaService.completeFindBookmarkByUrlEvent,
                twitterService.confirmAuthorisedEvent,
                BiFunction { t1, t2 ->
                    return@BiFunction Pair(t1, t2)
                }).subscribe { (editableBookmark, isAuthorizedTwitter) ->

            isFirstEdit.set(editableBookmark.isFirstEdit)
            comment.set(editableBookmark.comment)
            isOpen.set(!editableBookmark.isPrivate)
            isReadAfter.set(editableBookmark.tags.contains(HatenaService.TAG_READ_AFTER))
            this.tags = editableBookmark.tags
            this.isAuthorizedTwitter = isAuthorizedTwitter
        })

        registerDisposable(hatenaService.completeRegisterBookmarkEvent.subscribe {
            if (isShareTwitter.get()) {
                twitterService.postTweet(articleUrl, articleTitle.get(), comment.get())
            }
            progressDialog.dismiss()
            dismissDialogEventSubject.onNext(Unit)
        }, hatenaService.failAuthorizeHatenaEvent.subscribe {
            navigator.navigateToOAuth()
            dismissDialogEventSubject.onNext(Unit)
        }, hatenaService.completeDeleteBookmarkEvent.subscribe {
            progressDialog.dismiss()
            dismissDialogEventSubject.onNext(Unit)
        }, hatenaService.error.subscribe {
            snackbarFactory?.create(R.string.message_error_network)?.show()
        })
    }

    override fun onResume() {
        super.onResume()
        hatenaService.findBookmarkByUrl(articleUrl)
        twitterService.confirmAuthorised()
    }

    fun onDestroyView() {
        snackbarFactory = null
    }

    fun onClickOk(view: View) {

        progressDialog.show()

        if (isDelete.get()) {
            hatenaService.deleteBookmark(articleUrl)
        } else {
            hatenaService.registerBookmark(articleUrl,
                    comment.get(),
                    isOpen.get(),
                    isReadAfter.get(),
                    tags)
        }
    }

    fun onClickCancel(view: View) {
        dismissDialogEventSubject.onNext(Unit)
    }
}
