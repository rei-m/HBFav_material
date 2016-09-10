package me.rei_m.hbfavmaterial.presentation.fragment

import android.content.DialogInterface
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.AppCompatButton
import android.view.View
import android.widget.EditText
import android.widget.TextView
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.testutil.DriverActivity
import me.rei_m.hbfavmaterial.testutil.bindView
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil

@RunWith(RobolectricTestRunner::class)
class EditUserIdDialogFragmentTest {

    lateinit var fragment: EditUserIdDialogFragment

    private val holder: ViewHolder by lazy {
        val view = fragment.view ?: throw IllegalStateException("fragment's view is Null")
        ViewHolder(view)
    }

    @Before
    fun setUp() {
        fragment = EditUserIdDialogFragment.newInstance()
        SupportFragmentTestUtil.startFragment(fragment, CustomDriverActivity::class.java)
    }

    @Test
    fun testInitialize() {
        assertThat(holder.textTitle.visibility, `is`(View.VISIBLE))
        assertThat(holder.editUserId.visibility, `is`(View.VISIBLE))
        assertThat(holder.buttonOk.visibility, `is`(View.VISIBLE))
        assertThat(holder.buttonCancel.visibility, `is`(View.VISIBLE))
    }

    @Test
    fun testOnChangeEditUserId() {
        assertThat(holder.buttonOk.isEnabled, `is`(false))
        holder.editUserId.setText("a")
        assertThat(holder.buttonOk.isEnabled, `is`(true))
        holder.editUserId.setText("")
        assertThat(holder.buttonOk.isEnabled, `is`(false))
    }

    @Test
    fun testOnClickButtonOk() {
        holder.editUserId.setText("a")
        holder.buttonOk.performClick()
        verify(fragment.presenter).onClickButtonOk("a")
    }

    @Test
    fun testSetEditUserId() {
        holder.editUserId.setText("")
        fragment.setEditUserId("hoge")
        assertThat(holder.editUserId.text.toString(), `is`("hoge"))
    }

    @Test
    fun testShowHideProgress() {
        fragment.showProgress()
        assertThat(fragment.progressDialog?.isShowing, `is`(true))
        fragment.hideProgress()
        assertNull(fragment.progressDialog)
    }

    @Test
    fun testDisplayInvalidUserIdMessage() {
        holder.textInputLayout.error = ""
        fragment.displayInvalidUserIdMessage()
        assertThat(holder.textInputLayout.error.toString(), `is`(fragment.getString(R.string.message_error_input_user_id)))
    }

    class ViewHolder(view: View) {
        val textTitle by view.bindView<TextView>(R.id.dialog_fragment_edit_user_id_text_title)
        val editUserId by view.bindView<EditText>(R.id.dialog_fragment_edit_user_id_edit_user_id)
        val textInputLayout by view.bindView<TextInputLayout>(R.id.dialog_fragment_edit_user_id_layout_edit_user)
        val buttonCancel by view.bindView<AppCompatButton>(R.id.dialog_fragment_edit_user_id_button_cancel)
        val buttonOk by view.bindView<AppCompatButton>(R.id.dialog_fragment_edit_user_id_button_ok)
    }

    class CustomDriverActivity : DriverActivity(), DialogInterface {

        var isDismissed = false

        var isCanceled = false

        override fun dismiss() {
            isDismissed = true
        }

        override fun cancel() {
            isCanceled = true
        }
    }
}
