package me.rei_m.hbfavmaterial.presentation.fragment

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.testutil.DriverActivity
import me.rei_m.hbfavmaterial.testutil.bindView
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil

@RunWith(RobolectricTestRunner::class)
class SettingFragmentTest {

    lateinit var fragment: SettingFragment

    private val holder: ViewHolder by lazy {
        val view = fragment.view ?: throw IllegalStateException("fragment's view is Null")
        ViewHolder(view)
    }

    private val snackbarTextView: TextView
        get() = fragment.activity.findViewById(android.support.design.R.id.snackbar_text) as TextView

    @Before
    fun setUp() {

        fragment = SettingFragment.newInstance()

        SupportFragmentTestUtil.startFragment(fragment, CustomDriverActivity::class.java)
    }

    @Test
    fun testInitialize() {
        assertThat(holder.layoutHatenaId.visibility, `is`(View.VISIBLE))
        assertThat(holder.layoutHatenaOAuth.visibility, `is`(View.VISIBLE))
        assertThat(holder.layoutTwitterOAuth.visibility, `is`(View.VISIBLE))
    }

    @Test
    fun testSetUserId() {

        holder.textHatenaId.text = ""

        fragment.setUserId("hoge")

        assertThat(holder.textHatenaId.text.toString(), `is`("hoge"))
    }

    @Test
    fun testUpdateUserId() {
        holder.textHatenaId.text = ""

        val activity = fragment.activity as CustomDriverActivity

        activity.updatedUserId = ""
        fragment.updateUserId("hoge")

        assertThat(holder.textHatenaId.text.toString(), `is`("hoge"))
        assertThat(activity.updatedUserId, `is`("hoge"))
    }

    @Test
    fun testSetHatenaAuthoriseStatus() {

        holder.textUserOAuth.text = ""

        fragment.setHatenaAuthoriseStatus(true)
        assertThat(holder.textUserOAuth.text.toString(), `is`(fragment.getString(R.string.text_hatena_account_connect_ok)))

        fragment.setHatenaAuthoriseStatus(false)
        assertThat(holder.textUserOAuth.text.toString(), `is`(fragment.getString(R.string.text_hatena_account_connect_no)))
    }

    @Test
    fun testSetTwitterAuthoriseStatus() {
        holder.textTwitterOAuth.text = ""

        fragment.setTwitterAuthoriseStatus(true)
        assertThat(holder.textTwitterOAuth.text.toString(), `is`(fragment.getString(R.string.text_hatena_account_connect_ok)))

        fragment.setTwitterAuthoriseStatus(false)
        assertThat(holder.textTwitterOAuth.text.toString(), `is`(fragment.getString(R.string.text_hatena_account_connect_no)))
    }

    @Test
    fun testShowNetworkErrorMessage() {
        fragment.showNetworkErrorMessage()
        assertThat(snackbarTextView.visibility, `is`(View.VISIBLE))
        assertThat(snackbarTextView.text.toString(), `is`(fragment.getString(R.string.message_error_network)))
    }

    class CustomDriverActivity : DriverActivity(),
            SettingFragment.OnFragmentInteractionListener {

        var updatedUserId = ""

        override fun onUserIdUpdated(userId: String) {
            updatedUserId = userId
        }
    }

    class ViewHolder(view: View) {
        val layoutHatenaId by view.bindView<LinearLayout>(R.id.fragment_setting_layout_text_hatena_id)
        val textHatenaId by view.bindView<TextView>(R.id.fragment_setting_text_user_id)
        val layoutHatenaOAuth by view.bindView<LinearLayout>(R.id.fragment_setting_layout_text_hatena_oauth)
        val textUserOAuth by view.bindView<TextView>(R.id.fragment_setting_text_user_oauth)
        val layoutTwitterOAuth by view.bindView<LinearLayout>(R.id.fragment_setting_layout_text_twitter_oauth)
        val textTwitterOAuth by view.bindView<TextView>(R.id.fragment_setting_text_twitter_oauth)
    }
}