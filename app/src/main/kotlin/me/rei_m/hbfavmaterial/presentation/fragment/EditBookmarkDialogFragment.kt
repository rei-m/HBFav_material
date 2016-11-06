package me.rei_m.hbfavmaterial.presentation.fragment

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
import me.rei_m.hbfavmaterial.di.EditBookmarkDialogFragmentComponent
import me.rei_m.hbfavmaterial.di.EditBookmarkDialogFragmentModule
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEditEntity
import me.rei_m.hbfavmaterial.domain.service.HatenaService
import me.rei_m.hbfavmaterial.extension.*
import me.rei_m.hbfavmaterial.presentation.activity.SettingActivity
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

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

    @Inject
    lateinit var presenter: EditBookmarkDialogContact.Actions

    private var subscription: CompositeSubscription? = null

    private val bookmarkUrl: String by lazy {
        arguments.getString(ARG_BOOKMARK_URL)
    }

    private val bookmarkTitle: String by lazy {
        arguments.getString(ARG_BOOKMARK_TITLE)
    }

    private val bookmarkEdit: BookmarkEditEntity? by lazy {
        arguments.getSerializable(ARG_BOOKMARK)?.let {
            it as BookmarkEditEntity
        }
    }

    override var progressDialog: ProgressDialog? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window.requestFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as HasComponent<Injector>).getComponent()
                .plus(EditBookmarkDialogFragmentModule(context))
                .inject(this)
        presenter.onCreate(this, bookmarkUrl, bookmarkTitle, bookmarkEdit)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        subscription = CompositeSubscription()

        val view = inflater.inflate(R.layout.dialog_fragment_edit_bookmark, container, false)

        val textTitle = view.findViewById(R.id.dialog_fragment_edit_bookmark_text_title) as AppCompatTextView
        textTitle.text = getString(R.string.dialog_title_add_bookmark)

        with(view.findViewById(R.id.dialog_fragment_edit_bookmark_text_article_title) as AppCompatTextView) {
            text = bookmarkTitle
        }

        val editBookmark = view.findViewById(R.id.dialog_fragment_edit_bookmark_edit_bookmark) as EditText

        val switchOpen = view.findViewById(R.id.dialog_fragment_edit_bookmark_switch_open) as SwitchCompat
        switchOpen.setOnCheckedChangeListener { buttonView, isChecked ->
            presenter.onCheckedChangeOpen(isChecked)
        }

        val switchShareTwitter = view.findViewById(R.id.dialog_fragment_edit_bookmark_switch_share_twitter) as SwitchCompat
        switchShareTwitter.setOnCheckedChangeListener { buttonView, isChecked ->
            presenter.onCheckedChangeShareTwitter(isChecked)
        }

        val switchReadAfter = view.findViewById(R.id.dialog_fragment_edit_bookmark_switch_read_after) as SwitchCompat
        switchReadAfter.setOnCheckedChangeListener { buttonView, isChecked ->
            presenter.onCheckedChangeReadAfter(isChecked)
        }

        val switchDelete = view.findViewById(R.id.dialog_fragment_edit_bookmark_switch_delete) as SwitchCompat
        switchDelete.setOnCheckedChangeListener { buttonView, isChecked ->
            presenter.onCheckedChangeDelete(isChecked)
        }

        val buttonCancel = view.findViewById(R.id.dialog_fragment_edit_bookmark_button_cancel) as AppCompatButton
        buttonCancel.setOnClickListener { v ->
            dismiss()
        }

        val buttonOk = view.findViewById(R.id.dialog_fragment_edit_bookmark_button_ok) as AppCompatButton
        buttonOk.setOnClickListener { v ->
            presenter.onClickButtonOk(switchDelete.isChecked,
                    editBookmark.editableText.toString(),
                    switchOpen.isChecked,
                    switchReadAfter.isChecked,
                    switchShareTwitter.isChecked)
        }

        bookmarkEdit?.let {
            textTitle.text = resources.getString(R.string.dialog_title_update_bookmark)
            switchReadAfter.isChecked = it.tags.contains(HatenaService.TAG_READ_AFTER)
            editBookmark.setText(it.comment)
            switchOpen.isChecked = !it.isPrivate
            buttonOk.text = resources.getString(R.string.button_update)
        } ?: let {
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

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
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

    override fun setSwitchOpenCheck(isChecked: Boolean) {
        view?.findViewById(R.id.dialog_fragment_edit_bookmark_switch_open)?.let {
            it as SwitchCompat
            it.isChecked = isChecked
            it.text = if (isChecked) {
                context.resources.getString(R.string.text_open)
            } else {
                context.resources.getString(R.string.text_not_open)
            }
        }
    }

    override fun setSwitchShareTwitterCheck(isChecked: Boolean) {
        view?.findViewById(R.id.dialog_fragment_edit_bookmark_switch_share_twitter)?.let {
            it as SwitchCompat
            it.isChecked = isChecked
        }
    }

    override fun setSwitchReadAfterCheck(isChecked: Boolean) {
        view?.findViewById(R.id.dialog_fragment_edit_bookmark_switch_read_after)?.let {
            it as SwitchCompat
            it.isChecked = isChecked
        }
    }

    override fun setSwitchEnableByDelete(isEnabled: Boolean) {
        view?.let {
            it.findViewById(R.id.dialog_fragment_edit_bookmark_switch_open).isEnabled = isEnabled
            it.findViewById(R.id.dialog_fragment_edit_bookmark_switch_share_twitter).isEnabled = isEnabled
            it.findViewById(R.id.dialog_fragment_edit_bookmark_switch_read_after).isEnabled = isEnabled
            it.findViewById(R.id.dialog_fragment_edit_bookmark_edit_bookmark).isEnabled = isEnabled
        }
    }

    override fun showNetworkErrorMessage() {
        view?.findViewById(R.id.dialog_fragment_edit_bookmark_layout_root)?.let {
            (activity as AppCompatActivity).showSnackbarNetworkError(it)
        }
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

    interface Injector {
        fun plus(fragmentModule: EditBookmarkDialogFragmentModule): EditBookmarkDialogFragmentComponent
    }
}
