package me.rei_m.hbfavmaterial.fragments

import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import com.jakewharton.rxbinding.widget.RxTextView
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.activities.BaseActivity
import me.rei_m.hbfavmaterial.activities.SettingActivity
import me.rei_m.hbfavmaterial.entities.BookmarkEditEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.network.HatenaDeleteBookmarkLoadedEvent
import me.rei_m.hbfavmaterial.events.network.HatenaPostBookmarkLoadedEvent
import me.rei_m.hbfavmaterial.events.network.LoadedEventStatus
import me.rei_m.hbfavmaterial.extensions.*
import me.rei_m.hbfavmaterial.models.HatenaModel
import me.rei_m.hbfavmaterial.models.TwitterModel
import me.rei_m.hbfavmaterial.utils.BookmarkUtil
import rx.Subscription
import javax.inject.Inject

class EditBookmarkDialogFragment : DialogFragment(), ProgressDialogController {

    @Inject
    lateinit var hatenaModel: HatenaModel

    @Inject
    lateinit var twitterModel: TwitterModel

    override var progressDialog: ProgressDialog? = null

    lateinit private var mSubscription: Subscription

    companion object {

        val TAG: String = EditBookmarkDialogFragment::class.java.simpleName

        private val ARG_BOOKMARK_URL = "ARG_BOOKMARK_URL"

        private val ARG_BOOKMARK_TITLE = "ARG_BOOKMARK_TITLE"

        private val ARG_BOOKMARK = "ARG_BOOKMARK"

        fun newInstance(title: String,
                        url: String): EditBookmarkDialogFragment {
            return EditBookmarkDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_BOOKMARK_TITLE, title)
                    putString(ARG_BOOKMARK_URL, url)
                }
            }
        }

        fun newInstance(title: String,
                        url: String,
                        bookmarkEditEntity: BookmarkEditEntity): EditBookmarkDialogFragment {
            return newInstance(title, url).apply {
                arguments.putSerializable(ARG_BOOKMARK, bookmarkEditEntity)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window.requestFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as BaseActivity).component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.dialog_fragment_edit_bookmark, container, false)

        val bookmarkUrl = arguments.getString(ARG_BOOKMARK_URL)
        val bookmarkTitle = arguments.getString(ARG_BOOKMARK_TITLE)

        val bookmarkEdit: BookmarkEditEntity? = arguments.getSerializable(ARG_BOOKMARK)?.let {
            it as BookmarkEditEntity
        }

        val tags: MutableList<String>
        val isAdd: Boolean

        if (bookmarkEdit == null) {
            tags = arrayListOf<String>()
            isAdd = true
        } else {
            isAdd = false
            tags = bookmarkEdit.tags.toMutableList()
        }

        val textTitle = view.findViewById(R.id.dialog_fragment_edit_bookmark_text_title) as AppCompatTextView
        textTitle.text = getString(R.string.dialog_title_add_bookmark)

        with(view.findViewById(R.id.dialog_fragment_edit_bookmark_text_article_title) as AppCompatTextView) {
            text = bookmarkTitle
        }

        val editBookmark = view.findViewById(R.id.dialog_fragment_edit_bookmark_edit_bookmark) as EditText

        val switchOpen = view.findViewById(R.id.dialog_fragment_edit_bookmark_switch_open) as SwitchCompat
        val textOpen = context.resources.getString(R.string.text_open)
        val textNotOpen = context.resources.getString(R.string.text_not_open)
        switchOpen.setOnCheckedChangeListener { buttonView, isChecked ->
            buttonView.text = if (isChecked) {
                textOpen
            } else {
                textNotOpen
            }
        }

        val switchShareTwitter = view.findViewById(R.id.dialog_fragment_edit_bookmark_switch_share_twitter) as SwitchCompat
        with(switchShareTwitter) {
            if (twitterModel.isAuthorised()) {
                isChecked = twitterModel.isShare
            } else {
                isChecked = false
            }
            setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    if (!twitterModel.isAuthorised()) {
                        startActivity(SettingActivity.createIntent(activity))
                        dismiss()
                        return@setOnCheckedChangeListener
                    }
                }
                twitterModel.setIsShare(getAppContext(), isChecked)
            }
        }

        // あとで読むタグが登録済だったらチェックを有効にする
        val switchReadAfter = view.findViewById(R.id.dialog_fragment_edit_bookmark_switch_read_after) as SwitchCompat

        switchReadAfter.isChecked = tags.contains(HatenaModel.TAG_READ_AFTER)

        val switchDelete = view.findViewById(R.id.dialog_fragment_edit_bookmark_switch_delete) as SwitchCompat

        switchDelete.setOnCheckedChangeListener { buttonView, isChecked ->
            switchOpen.isEnabled = !isChecked
            switchShareTwitter.isEnabled = !isChecked
            switchReadAfter.isEnabled = !isChecked
            editBookmark.isEnabled = !isChecked
        }

        val buttonCancel = view.findViewById(R.id.dialog_fragment_edit_bookmark_button_cancel) as AppCompatButton
        buttonCancel.setOnClickListener { v ->
            dismiss()
        }

        val buttonOk = view.findViewById(R.id.dialog_fragment_edit_bookmark_button_ok) as AppCompatButton
        buttonOk.setOnClickListener { v ->
            if (switchDelete.isChecked) {
                hatenaModel.deleteBookmark(bookmarkUrl)
            } else {
                val inputtedComment = editBookmark.editableText.toString()

                if (switchReadAfter.isChecked) {
                    if (!tags.contains(HatenaModel.TAG_READ_AFTER)) {
                        tags.add(HatenaModel.TAG_READ_AFTER)
                    }
                } else {
                    if (tags.contains(HatenaModel.TAG_READ_AFTER)) {
                        tags.remove(HatenaModel.TAG_READ_AFTER)
                    }
                }

                hatenaModel.registerBookmark(bookmarkUrl,
                        inputtedComment,
                        switchOpen.isChecked,
                        tags)
                if (switchShareTwitter.isChecked) {
                    twitterModel.postTweet(BookmarkUtil.createShareText(bookmarkUrl, bookmarkTitle, inputtedComment))
                }
            }
            showProgressDialog(activity)
        }

        val textCommentCount = view.findViewById(R.id.dialog_fragment_edit_bookmark_text_comment_char_count) as AppCompatTextView

        val commentLength = resources.getInteger(R.integer.bookmark_comment_length)

        mSubscription = RxTextView.textChanges(editBookmark)
                .map { v ->
                    Math.ceil(v.toString().toByteArray().size / 3.0).toInt()
                }
                .subscribe { size ->
                    textCommentCount.let {
                        it.text = "$size / $commentLength"
                        if (commentLength < size) {
                            it.setTextColor(Color.RED)
                            buttonOk.disable()
                        } else {
                            it.setTextColor(R.color.text_color_thin)
                            buttonOk.enable()
                        }
                    }
                }

        if (!isAdd) {
            val bookmark = arguments.getSerializable(ARG_BOOKMARK) as BookmarkEditEntity
            textTitle.text = resources.getString(R.string.dialog_title_update_bookmark)
            editBookmark.setText(bookmark.comment)
            switchOpen.isChecked = !bookmark.isPrivate
            buttonOk.text = resources.getString(R.string.button_update)
        } else {
            switchDelete.hide()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSubscription.unsubscribe()
    }

    override fun onResume() {
        super.onResume()
        EventBusHolder.EVENT_BUS.register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBusHolder.EVENT_BUS.unregister(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adjustScreenWidth()
    }

    @Subscribe
    fun subscribe(event: HatenaPostBookmarkLoadedEvent) {

        closeProgressDialog()

        when (event.status) {
            LoadedEventStatus.OK -> {
                dismiss()
            }
            else -> {
                (activity as AppCompatActivity).showSnackbarNetworkError(view)
            }
        }
    }

    @Subscribe
    fun subscribe(event: HatenaDeleteBookmarkLoadedEvent) {

        closeProgressDialog()

        when (event.status) {
            LoadedEventStatus.OK -> {
                dismiss()
            }
            LoadedEventStatus.NOT_FOUND -> {
                dismiss()
            }
            else -> {
                (activity as AppCompatActivity).showSnackbarNetworkError(view)
            }
        }
    }
}
