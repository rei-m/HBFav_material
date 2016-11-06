package me.rei_m.hbfavmaterial.presentation.fragment

import android.support.design.widget.TextInputLayout
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.di.InitializeFragmentComponent
import me.rei_m.hbfavmaterial.di.InitializeFragmentModule
import me.rei_m.hbfavmaterial.di.SplashActivityComponent
import me.rei_m.hbfavmaterial.presentation.manager.ActivityNavigator
import me.rei_m.hbfavmaterial.testutil.DriverActivity
import me.rei_m.hbfavmaterial.testutil.bindView
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil

@RunWith(RobolectricTestRunner::class)
class InitializeFragmentTest {

    lateinit var fragment: InitializeFragment

    private val holder: ViewHolder by lazy {
        val view = fragment.view ?: throw IllegalStateException("fragment's view is Null")
        ViewHolder(view)
    }

    private val snackbarTextView: TextView
        get() = fragment.activity.findViewById(android.support.design.R.id.snackbar_text) as TextView

    private fun getString(resId: Int): String {
        return fragment.getString(resId)
    }

    @Mock
    lateinit var navigator: ActivityNavigator

    @Mock
    lateinit var presenter: InitializeContact.Actions

    @Before
    fun setUp() {

        MockitoAnnotations.initMocks(this)

        fragment = InitializeFragment.newInstance()
        fragment.presenter = presenter
        fragment.navigator = navigator

        SupportFragmentTestUtil.startFragment(fragment, CustomDriverActivity::class.java)
    }

    @Test
    fun initialize() {
        assertThat(holder.editHatenaId.visibility, `is`(View.VISIBLE))
        assertThat(holder.buttonSetHatenaId.visibility, `is`(View.VISIBLE))
        assertThat(holder.buttonSetHatenaId.isEnabled, `is`(false))
    }

    @Test
    fun testButtonSetHatenaIdStatus_input_id() {
        holder.editHatenaId.setText("a")
        assertThat(holder.buttonSetHatenaId.isEnabled, `is`(true))
    }

    @Test
    fun testButtonSetHatenaIdStatus_not_input_id() {
        holder.editHatenaId.setText("")
        assertThat(holder.buttonSetHatenaId.isEnabled, `is`(false))
    }

    @Test
    fun testButtonSetHatenaIdClick() {
        val presenter = mock(InitializeContact.Actions::class.java)
        fragment.presenter = presenter
        holder.editHatenaId.setText("valid")
        holder.buttonSetHatenaId.performClick()
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
        assertThat(holder.textInputLayoutHatenaId.error.toString(), `is`(getString(R.string.message_error_input_user_id)))
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
        fragment.navigateToMain()
        verify(navigator).navigateToMain(fragment.activity)
    }

    class ViewHolder(view: View) {
        val editHatenaId by view.bindView<EditText>(R.id.fragment_initialize_edit_hatena_id)
        val buttonSetHatenaId by view.bindView<Button>(R.id.fragment_initialize_button_set_hatena_id)
        val textInputLayoutHatenaId by view.bindView<TextInputLayout>(R.id.fragment_initialize_layout_hatena_id)
    }

    class CustomDriverActivity : DriverActivity(),
            HasComponent<SplashActivityComponent> {

        override fun getComponent(): SplashActivityComponent {
            val activityComponent = mock(SplashActivityComponent::class.java)

            `when`(activityComponent.plus(any(InitializeFragmentModule::class.java)))
                    .thenReturn(mock(InitializeFragmentComponent::class.java))

            return activityComponent
        }
    }
}
