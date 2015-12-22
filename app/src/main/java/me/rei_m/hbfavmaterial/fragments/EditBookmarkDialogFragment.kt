package me.rei_m.hbfavmaterial.fragments

import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TextInputLayout
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
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.HatenaRestApiBookmarkResponse
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.network.HatenaDeleteBookmarkLoadedEvent
import me.rei_m.hbfavmaterial.events.network.HatenaPostBookmarkLoadedEvent
import me.rei_m.hbfavmaterial.events.network.LoadedEventStatus
import me.rei_m.hbfavmaterial.extensions.*
import me.rei_m.hbfavmaterial.managers.ModelLocator
import me.rei_m.hbfavmaterial.models.HatenaModel
import rx.Subscription

public class EditBookmarkDialogFragment : DialogFragment(), ProgressDialogI {

    override var mProgressDialog: ProgressDialog? = null

    private var mLayoutBookmark: TextInputLayout? = null

    private var mSubscription: Subscription? = null

    companion object {

        public final val TAG = EditBookmarkDialogFragment::class.java.simpleName

        private final val ARG_BOOKMARK_URL = "ARG_BOOKMARK_URL"

        private final val ARG_BOOKMARK_TITLE = "ARG_BOOKMARK_TITLE"

        private final val ARG_BOOKMARK = "ARG_BOOKMARK"

        public fun newInstance(title: String,
                               url: String): EditBookmarkDialogFragment {
            return EditBookmarkDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_BOOKMARK_TITLE, title)
                    putString(ARG_BOOKMARK_URL, url)
                }
            }
        }

        public fun newInstance(title: String,
                               url: String,
                               response: HatenaRestApiBookmarkResponse): EditBookmarkDialogFragment {
            return newInstance(title, url).apply {
                arguments.putSerializable(ARG_BOOKMARK, response)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog? {
        return super.onCreateDialog(savedInstanceState).apply {
            window.requestFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_fragment_edit_bookmark, null)

        val hatenaModel = ModelLocator.get(ModelLocator.Companion.Tag.HATENA) as HatenaModel

        val bookmarkUrl = arguments.getString(ARG_BOOKMARK_URL)

        val isAdd = (arguments.getSerializable(ARG_BOOKMARK) == null)

        val textTitle = view.findViewById(R.id.dialog_fragment_edit_bookmark_text_title) as AppCompatTextView
        textTitle.text = getString(R.string.dialog_title_add_bookmark)

        val textBookmarkTitle = view.findViewById(R.id.dialog_fragment_edit_bookmark_text_article_title) as AppCompatTextView
        textBookmarkTitle.text = arguments.getString(ARG_BOOKMARK_TITLE)

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

        val switchDelete = view.findViewById(R.id.dialog_fragment_edit_bookmark_switch_delete) as SwitchCompat
        switchDelete.setOnCheckedChangeListener { buttonView, isChecked ->
            switchOpen.isEnabled = !isChecked
            editBookmark.isEnabled = !isChecked
        }

        mLayoutBookmark = view.findViewById(R.id.dialog_fragment_edit_bookmark_layout_edit_bookmark) as TextInputLayout

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
                hatenaModel.registerBookmark(bookmarkUrl, inputtedComment, switchOpen.isChecked)

            }
            showProgressDialog(activity)
        }

        val textCommentCount = view.findViewById(R.id.dialog_fragment_edit_bookmark_text_comment_char_count) as AppCompatTextView

        val commentLength = resources.getInteger(R.integer.bookmark_comment_length)

        val editBookmarkStream = RxTextView.textChanges(editBookmark)
        mSubscription = editBookmarkStream
                .map { v ->
                    Math.ceil(v.toString().toByteArray().size / 3.0).toInt()
                }
                .subscribe { size ->
                    textCommentCount.text = "$size / $commentLength"
                    if (commentLength < size) {
                        textCommentCount.setTextColor(Color.RED)
                        buttonOk.disable()
                    } else {
                        textCommentCount.setTextColor(R.color.text_color_thin)
                        buttonOk.enable()
                    }
                }

        if (!isAdd) {
            val bookmark = arguments.getSerializable(ARG_BOOKMARK) as HatenaRestApiBookmarkResponse
            textTitle.text = resources.getString(R.string.dialog_title_update_bookmark)
            editBookmark.setText(bookmark.comment)
            switchOpen.isChecked = !bookmark.private
            buttonOk.text = resources.getString(R.string.button_update)
        } else {
            switchDelete.hide()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSubscription?.unsubscribe()
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
    public fun onHatenaPostBookmarkLoaded(event: HatenaPostBookmarkLoadedEvent) {

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
    public fun onHatenaDeleteBookmarkLoaded(event: HatenaDeleteBookmarkLoadedEvent) {

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