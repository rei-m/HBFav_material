package me.rei_m.hbfavmaterial.presentation.fragment

import android.support.design.widget.TextInputLayout
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.presentation.fragment.InitializeContact
import me.rei_m.hbfavmaterial.testutil.DriverActivity
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil

@RunWith(RobolectricTestRunner::class)
class InitializeFragmentTest {

    lateinit var fragment: InitializeFragment

    private val view: View by lazy {
        fragment.view ?: throw IllegalStateException("fragment's view is Null")
    }

    private val editHatenaId: EditText
        get() = view.findViewById(R.id.fragment_initialize_edit_hatena_id) as EditText

    private val buttonSetHatenaId: Button
        get() = view.findViewById(R.id.fragment_initialize_button_set_hatena_id) as Button

    private val textInputLayoutHatenaId: TextInputLayout
        get() = view.findViewById(R.id.fragment_initialize_layout_hatena_id) as TextInputLayout

    private val snackbarTextView: TextView
        get() = fragment.activity.findViewById(android.support.design.R.id.snackbar_text) as TextView

    private fun getString(resId: Int): String {
        return fragment.getString(resId)
    }

    @Before
    fun setUp() {
        fragment = InitializeFragment.newInstance()
        SupportFragmentTestUtil.startFragment(fragment, DriverActivity::class.java)
    }

    @Test
    fun initialize() {
        assertThat(editHatenaId.visibility, `is`(View.VISIBLE))
        assertThat(buttonSetHatenaId.visibility, `is`(View.VISIBLE))
        assertThat(buttonSetHatenaId.isEnabled, `is`(false))
    }

    @Test
    fun testButtonSetHatenaIdStatus_input_id() {
        editHatenaId.setText("a")
        assertThat(buttonSetHatenaId.isEnabled, `is`(true))
    }

    @Test
    fun testButtonSetHatenaIdStatus_not_input_id() {
        editHatenaId.setText("")
        assertThat(buttonSetHatenaId.isEnabled, `is`(false))
    }

    @Test
    fun testButtonSetHatenaIdClick() {
        val presenter = mock(InitializeContact.Actions::class.java)
        fragment.presenter = presenter
        editHatenaId.setText("valid")
        buttonSetHatenaId.performClick()
        verify(presenter).onClickButtonSetId("valid")
    }

    @Test
    fun testShowNetworkErrorMessage() {
        fragment.showNetworkErrorMessage()
        assertThat(snackbarTextView.visibility, `is`(View.VISIBLE))
        assertThat(snackbarTextView.text.toString(), `is`(getString(R.string.message_error_network)))
    }

    @Test
    fun testDisplayInvalidUserIdMessage() {
        fragment.displayInvalidUserIdMessage()
        assertThat(textInputLayoutHatenaId.error.toString(), `is`(getString(R.string.message_error_input_user_id)))
    }

    @Test
    fun testShowHideProgress() {
        fragment.showProgress()
        assertThat(fragment.progressDialog?.isShowing, `is`(true))
        fragment.hideProgress()
        assertNull(fragment.progressDialog)
    }

    @Test
    fun testNavigateToMain() {
        val navigator = spy(fragment.navigator)
        fragment.navigator = navigator
        fragment.navigateToMain()
        verify(navigator).navigateToMain(fragment.activity)
    }
}
