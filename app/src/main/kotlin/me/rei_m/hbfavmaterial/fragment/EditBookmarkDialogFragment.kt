package me.rei_m.hbfavmaterial.fragment

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
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.activitiy.BaseActivity
import me.rei_m.hbfavmaterial.activitiy.SettingActivity
import me.rei_m.hbfavmaterial.entitiy.BookmarkEditEntity
import me.rei_m.hbfavmaterial.extension.*
import me.rei_m.hbfavmaterial.fragment.presenter.EditBookmarkDialogContact
import me.rei_m.hbfavmaterial.fragment.presenter.EditBookmarkDialogPresenter
import me.rei_m.hbfavmaterial.service.HatenaService
import rx.subscriptions.CompositeSubscription

class EditBookmarkDialogFragment : DialogFragment(),
        EditBookmarkDialogContact.View,
        ProgressDialogController {

    companion object {

        val TAG: String = EditBookmarkDialogFragment::class.java.simpleName

        private const val ARG_BOOKMARK_URL = "ARG_BOOKMARK_URL"

        private const val ARG_BOOKMARK_TITLE = "ARG_BOOKMARK_TITLE"

        private const val ARG_BOOKMARK = "ARG_BOOKMARK"

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

    private lateinit var presenter: EditBookmarkDialogPresenter

    private var subscription: CompositeSubscription? = null

    override var progressDialog: ProgressDialog? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window.requestFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = EditBookmarkDialogPresenter(this)
        val component = (activity as BaseActivity).component
        component.inject(presenter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        subscription = CompositeSubscription()

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
            setOnCheckedChangeListener { buttonView, isChecked ->
                presenter.changeCheckedShareTwitter(isChecked)
            }
        }

        // あとで読むタグが登録済だったらチェックを有効にする
        val switchReadAfter = view.findViewById(R.id.dialog_fragment_edit_bookmark_switch_read_after) as SwitchCompat

        switchReadAfter.isChecked = tags.contains(HatenaService.TAG_READ_AFTER)

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

                presenter.deleteBookmark(bookmarkUrl)

            } else {
                val inputtedComment = editBookmark.editableText.toString()

                if (switchReadAfter.isChecked) {
                    if (!tags.contains(HatenaService.TAG_READ_AFTER)) {
                        tags.add(HatenaService.TAG_READ_AFTER)
                    }
                } else {
                    if (tags.contains(HatenaService.TAG_READ_AFTER)) {
                        tags.remove(HatenaService.TAG_READ_AFTER)
                    }
                }

                presenter.registerBookmark(bookmarkUrl,
                        bookmarkTitle,
                        inputtedComment,
                        switchOpen.isChecked,
                        tags,
                        switchShareTwitter.isChecked)
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

        val textCommentCount = view.findViewById(R.id.dialog_fragment_edit_bookmark_text_comment_char_count) as AppCompatTextView

        val commentLength = resources.getInteger(R.integer.bookmark_comment_length)

        subscription?.add(RxTextView.textChanges(editBookmark)
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
                })

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewCreated()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscription?.unsubscribe()
        subscription = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adjustScreenWidth()
    }

    override fun setSwitchShareTwitterCheck(isChecked: Boolean) {
        view?.findViewById(R.id.dialog_fragment_edit_bookmark_switch_share_twitter)?.let {
            it as SwitchCompat
            it.isChecked = isChecked
        }
    }

    override fun showNetworkErrorMessage() {
        (activity as AppCompatActivity).showSnackbarNetworkError(view)
    }

    override fun showProgress() {
        showProgressDialog(activity)
    }

    override fun hideProgress() {
        closeProgressDialog()
    }

    override fun startSettingActivity() {
        startActivity(SettingActivity.createIntent(activity))
    }

    override fun dismissDialog() {
        dismiss()
    }
}
