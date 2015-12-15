package me.rei_m.hbfavmaterial.fragments

import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.widget.EditText
import com.jakewharton.rxbinding.widget.RxTextView
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.extensions.disable
import me.rei_m.hbfavmaterial.extensions.enable
import me.rei_m.hbfavmaterial.managers.ModelLocator
import me.rei_m.hbfavmaterial.models.HatenaModel
import rx.Subscription

public class EditBookmarkDialogFragment : DialogFragment(), ProgressDialogI {

    override var mProgressDialog: ProgressDialog? = null

    private var mLayoutBookmark: TextInputLayout? = null

    private var mSubscription: Subscription? = null

    companion object {

        public final val TAG = EditBookmarkDialogFragment::class.java.simpleName

        private final val ARG_BOOKMARK_TITLE = "ARG_BOOKMARK_TITLE"

        public fun newInstance(title: String): EditBookmarkDialogFragment {

            val fragment = EditBookmarkDialogFragment()
            val args = Bundle()
            args.putString(ARG_BOOKMARK_TITLE, title)
            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog? {

        val hatenaModel = ModelLocator.get(ModelLocator.Companion.Tag.HATENA) as HatenaModel

        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_fragment_edit_bookmark, null)

        val textTitle = view.findViewById(R.id.dialog_fragment_edit_bookmark_text_title) as AppCompatTextView
        textTitle.text = arguments.getString(ARG_BOOKMARK_TITLE)


        val editBookmark = view.findViewById(R.id.dialog_fragment_edit_bookmark_edit_bookmark) as EditText
        //        editUserId.setText(userModel.userEntity?.id)

        mLayoutBookmark = view.findViewById(R.id.dialog_fragment_edit_bookmark_layout_edit_bookmark) as TextInputLayout

        val buttonCancel = view.findViewById(R.id.dialog_fragment_edit_bookmark_button_cancel) as AppCompatButton
        buttonCancel.setOnClickListener({ v ->
            dismiss()
        })

        val buttonOk = view.findViewById(R.id.dialog_fragment_edit_bookmark_button_ok) as AppCompatButton
        buttonOk.setOnClickListener({ v ->
            //            val inputtedUserId = editUserId.editableText.toString()
            //            if (inputtedUserId != userModel.userEntity?.id) {
            //                userModel.checkAndSaveUserId(getAppContext(), editUserId.editableText.toString())
            //                showProgressDialog(activity)
            //            } else {
            //                dismiss()
            //            }
        })

        val textCommentCount = view.findViewById(R.id.dialog_fragment_edit_bookmark_text_comment_char_count) as AppCompatTextView

        val commentLength = resources.getInteger(R.integer.bookmark_comment_length)

        val editBookmarkStream = RxTextView.textChanges(editBookmark)
        mSubscription = editBookmarkStream
                .map({ v ->
                    Math.ceil(v.toString().toByteArray().size / 3.0).toInt()
                })
                .subscribe({ size ->
                    textCommentCount.text = "$size / $commentLength"
                    if (commentLength < size) {
                        textCommentCount.setTextColor(Color.RED)
                        buttonOk.disable()
                    } else {
                        textCommentCount.setTextColor(R.color.text_color_thin)
                        buttonOk.enable()
                    }
                })

        val builder = AlertDialog.Builder(activity)
                .setTitle(getString(R.string.dialog_title_set_bookmark))
                .setView(view)

        return builder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSubscription?.unsubscribe()
    }

    override fun onResume() {
        super.onResume()

        // EventBus登録
        EventBusHolder.EVENT_BUS.register(this)
    }

    override fun onPause() {
        super.onPause()

        // EventBus登録解除
        EventBusHolder.EVENT_BUS.unregister(this)
    }

    //    @Subscribe
    //    public fun onUserIdChecked(event: UserIdCheckedEvent) {
    //
    //        closeProgressDialog()
    //
    //        when (event.type) {
    //            UserIdCheckedEvent.Companion.Type.OK -> {
    //                mLayoutUserId?.isErrorEnabled = false
    //                dismiss()
    //            }
    //
    //            UserIdCheckedEvent.Companion.Type.NG -> {
    //                mLayoutUserId?.error = getString(R.string.message_error_input_user_id)
    //            }
    //
    //            UserIdCheckedEvent.Companion.Type.ERROR -> {
    //                mLayoutUserId?.error = getString(R.string.message_error_network)
    //            }
    //        }
    //    }
}