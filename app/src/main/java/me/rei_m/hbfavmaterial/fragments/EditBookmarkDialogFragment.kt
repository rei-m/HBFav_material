package me.rei_m.hbfavmaterial.fragments

import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.jakewharton.rxbinding.widget.RxTextView
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.activities.SettingActivity
import me.rei_m.hbfavmaterial.databinding.DialogFragmentEditBookmarkBinding
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
import java.util.*
import javax.inject.Inject

class EditBookmarkDialogFragment : DialogFragment(), IProgressDialog {

    @Inject
    lateinit var hatenaModel: HatenaModel

    @Inject
    lateinit var twitterModel: TwitterModel

    override var mProgressDialog: ProgressDialog? = null

    lateinit private var mSubscription: Subscription

    companion object {

        val TAG = EditBookmarkDialogFragment::class.java.simpleName

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog? {
        return super.onCreateDialog(savedInstanceState).apply {
            window.requestFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.graph.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = DialogFragmentEditBookmarkBinding.inflate(inflater, container, false)

        val bookmarkUrl = arguments.getString(ARG_BOOKMARK_URL)
        val bookmarkTitle = arguments.getString(ARG_BOOKMARK_TITLE)

        val bookmarkEdit: BookmarkEditEntity? = arguments.getSerializable(ARG_BOOKMARK)?.let {
            it as BookmarkEditEntity
        }

        val tags: ArrayList<String>
        val isAdd: Boolean

        if (bookmarkEdit == null) {
            tags = ArrayList<String>()
            isAdd = true
        } else {
            isAdd = false
            tags = bookmarkEdit.tags
        }

        binding.dialogFragmentEditBookmarkTextTitle.text = getString(R.string.dialog_title_add_bookmark)

        binding.dialogFragmentEditBookmarkTextArticleTitle.text = bookmarkTitle

        val textOpen = context.resources.getString(R.string.text_open)
        val textNotOpen = context.resources.getString(R.string.text_not_open)
        binding.dialogFragmentEditBookmarkSwitchOpen.setOnCheckedChangeListener { buttonView, isChecked ->
            buttonView.text = if (isChecked) {
                textOpen
            } else {
                textNotOpen
            }
        }

        binding.dialogFragmentEditBookmarkSwitchShareTwitter.let {
            if (twitterModel.isAuthorised()) {
                it.isChecked = twitterModel.isShare
            } else {
                it.isChecked = false
            }
            it.setOnCheckedChangeListener { buttonView, isChecked ->
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

        // TODO TAGSにあとで読むがあったらチェックを有効にする

        binding.dialogFragmentEditBookmarkSwitchDelete.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.dialogFragmentEditBookmarkSwitchOpen.isEnabled = !isChecked
            binding.dialogFragmentEditBookmarkSwitchShareTwitter.isEnabled = !isChecked
            binding.dialogFragmentEditBookmarkSwitchReadAfter.isEnabled = !isChecked
            binding.dialogFragmentEditBookmarkEditBookmark.isEnabled = !isChecked
        }

        binding.dialogFragmentEditBookmarkButtonCancel.setOnClickListener { v ->
            dismiss()
        }

        binding.dialogFragmentEditBookmarkButtonOk.setOnClickListener { v ->
            if (binding.dialogFragmentEditBookmarkSwitchDelete.isChecked) {
                hatenaModel.deleteBookmark(bookmarkUrl)
            } else {
                val inputtedComment = binding.dialogFragmentEditBookmarkEditBookmark.editableText.toString()

                if (binding.dialogFragmentEditBookmarkSwitchReadAfter.isChecked) {
                    // TODO TAGSにあとで読むが無かったら追加する.
                    tags.add("あとで読む")
                } else {
                    // TODO TAGSにあとで読むがあったら削除する.
                }

                hatenaModel.registerBookmark(bookmarkUrl,
                        inputtedComment,
                        binding.dialogFragmentEditBookmarkSwitchOpen.isChecked,
                        tags)
                if (binding.dialogFragmentEditBookmarkSwitchShareTwitter.isChecked) {
                    twitterModel.postTweet(BookmarkUtil.createShareText(bookmarkUrl, bookmarkTitle, inputtedComment))
                }
            }
            showProgressDialog(activity)
        }

        val commentLength = resources.getInteger(R.integer.bookmark_comment_length)

        mSubscription = RxTextView.textChanges(binding.dialogFragmentEditBookmarkEditBookmark)
                .map { v ->
                    Math.ceil(v.toString().toByteArray().size / 3.0).toInt()
                }
                .subscribe { size ->
                    binding.dialogFragmentEditBookmarkTextCommentCharCount.let {
                        it.text = "$size / $commentLength"
                        if (commentLength < size) {
                            it.setTextColor(Color.RED)
                            binding.dialogFragmentEditBookmarkButtonOk.disable()
                        } else {
                            it.setTextColor(R.color.text_color_thin)
                            binding.dialogFragmentEditBookmarkButtonOk.enable()
                        }
                    }
                }

        if (!isAdd) {
            val bookmark = arguments.getSerializable(ARG_BOOKMARK) as BookmarkEditEntity
            binding.dialogFragmentEditBookmarkTextTitle.text = resources.getString(R.string.dialog_title_update_bookmark)
            binding.dialogFragmentEditBookmarkEditBookmark.setText(bookmark.comment)
            binding.dialogFragmentEditBookmarkSwitchOpen.isChecked = !bookmark.isPrivate
            binding.dialogFragmentEditBookmarkButtonOk.text = resources.getString(R.string.button_update)
        } else {
            binding.dialogFragmentEditBookmarkSwitchDelete.hide()
        }

        return binding.root
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
