package me.rei_m.hbfavmaterial.presentation.fragment

import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.SwitchCompat
import android.view.View
import android.widget.EditText
import android.widget.TextView
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.BookmarkActivityComponent
import me.rei_m.hbfavmaterial.di.EditBookmarkDialogFragmentComponent
import me.rei_m.hbfavmaterial.di.EditBookmarkDialogFragmentModule
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.testutil.DriverActivity
import me.rei_m.hbfavmaterial.testutil.bindView
import org.hamcrest.Matchers.`is`
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
class EditBookmarkDialogFragmentTest {

    lateinit var fragment: EditBookmarkDialogFragment

    private val holder: ViewHolder by lazy {
        val view = fragment.view ?: throw IllegalStateException("fragment's view is Null")
        ViewHolder(view)
    }

    private fun getString(resId: Int): String {
        return fragment.getString(resId)
    }

    @Mock
    lateinit var presenter: EditBookmarkDialogContact.Actions

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        fragment = EditBookmarkDialogFragment.newInstance("title", "url")
        fragment.presenter = presenter

        SupportFragmentTestUtil.startFragment(fragment, CustomDriverActivity::class.java)
    }

    @Test
    fun testInitialize() {

        assertThat(holder.textTitle.text.toString(), `is`(getString(R.string.dialog_title_add_bookmark)))
        assertThat(holder.textArticleTitle.text.toString(), `is`("title"))
        assertThat(holder.switchDelete.visibility, `is`(View.GONE))

        holder.switchOpen.performClick()
        verify(fragment.presenter).onCheckedChangeOpen(holder.switchOpen.isChecked)

        holder.switchShareTwitter.performClick()
        verify(fragment.presenter).onCheckedChangeShareTwitter(holder.switchShareTwitter.isChecked)

        holder.switchReadAfter.performClick()
        verify(fragment.presenter).onCheckedChangeReadAfter(holder.switchReadAfter.isChecked)

        holder.buttonOk.performClick()
        verify(fragment.presenter).onClickButtonOk(holder.switchDelete.isChecked,
                "",
                holder.switchOpen.isChecked,
                holder.switchReadAfter.isChecked,
                holder.switchShareTwitter.isChecked)
    }

    @Test
    fun testSetSwitchOpenCheck() {

        holder.switchOpen.isChecked = false

        fragment.setSwitchOpenCheck(true)
        assertThat(holder.switchOpen.isChecked, `is`(true))

        fragment.setSwitchOpenCheck(false)
        assertThat(holder.switchOpen.isChecked, `is`(false))
    }

    @Test
    fun testSetSwitchShareTwitterCheck() {

        holder.switchShareTwitter.isChecked = false

        fragment.setSwitchShareTwitterCheck(true)
        assertThat(holder.switchShareTwitter.isChecked, `is`(true))

        fragment.setSwitchShareTwitterCheck(false)
        assertThat(holder.switchShareTwitter.isChecked, `is`(false))
    }

    @Test
    fun testSetSwitchReadAfterCheck() {

        holder.switchReadAfter.isChecked = false

        fragment.setSwitchReadAfterCheck(true)
        assertThat(holder.switchReadAfter.isChecked, `is`(true))

        fragment.setSwitchReadAfterCheck(false)
        assertThat(holder.switchReadAfter.isChecked, `is`(false))
    }

    @Test
    fun testSetSwitchEnableByDelete() {
        holder.switchShareTwitter.isEnabled = false
        holder.switchReadAfter.isEnabled = false
        holder.switchOpen.isEnabled = false
        holder.editBookmark.isEnabled = false

        fragment.setSwitchEnableByDelete(true)
        assertThat(holder.switchShareTwitter.isEnabled, `is`(true))
        assertThat(holder.switchReadAfter.isEnabled, `is`(true))
        assertThat(holder.switchOpen.isEnabled, `is`(true))
        assertThat(holder.editBookmark.isEnabled, `is`(true))

        fragment.setSwitchEnableByDelete(false)
        assertThat(holder.switchShareTwitter.isEnabled, `is`(false))
        assertThat(holder.switchReadAfter.isEnabled, `is`(false))
        assertThat(holder.switchOpen.isEnabled, `is`(false))
        assertThat(holder.editBookmark.isEnabled, `is`(false))
    }

    @Test
    fun testShowHideProgress() {
        fragment.showProgress()
        assertThat(fragment.progressDialog?.isShowing, `is`(true))
        fragment.hideProgress()
        assertNull(fragment.progressDialog)
    }

    @Test
    fun testStartSettingActivity() {

    }

    class ViewHolder(view: View) {
        val textTitle by view.bindView<TextView>(R.id.dialog_fragment_edit_bookmark_text_title)
        val textArticleTitle by view.bindView<TextView>(R.id.dialog_fragment_edit_bookmark_text_article_title)
        val editBookmark by view.bindView<EditText>(R.id.dialog_fragment_edit_bookmark_edit_bookmark)
        val switchOpen by view.bindView<SwitchCompat>(R.id.dialog_fragment_edit_bookmark_switch_open)
        val switchShareTwitter by view.bindView<SwitchCompat>(R.id.dialog_fragment_edit_bookmark_switch_share_twitter)
        val switchReadAfter by view.bindView<SwitchCompat>(R.id.dialog_fragment_edit_bookmark_switch_read_after)
        val switchDelete by view.bindView<SwitchCompat>(R.id.dialog_fragment_edit_bookmark_switch_delete)
        val buttonCancel by view.bindView<AppCompatButton>(R.id.dialog_fragment_edit_bookmark_button_cancel)
        val buttonOk by view.bindView<AppCompatButton>(R.id.dialog_fragment_edit_bookmark_button_ok)
    }

    class CustomDriverActivity : DriverActivity(),
            HasComponent<BookmarkActivityComponent> {

        override fun getComponent(): BookmarkActivityComponent {
            val activityComponent = mock(BookmarkActivityComponent::class.java)

            `when`(activityComponent.plus(any(EditBookmarkDialogFragmentModule::class.java)))
                    .thenReturn(mock(EditBookmarkDialogFragmentComponent::class.java))

            return activityComponent
        }
    }
}
