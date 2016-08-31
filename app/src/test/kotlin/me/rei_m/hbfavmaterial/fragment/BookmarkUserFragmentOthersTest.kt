package me.rei_m.hbfavmaterial.fragment

import android.view.View
import me.rei_m.hbfavmaterial.testutil.DriverActivity
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil

@RunWith(RobolectricTestRunner::class)
class BookmarkUserFragmentOthersTest {

    lateinit var fragment: BookmarkUserFragment

    @Rule
    @JvmField
    val thrown = ExpectedException.none()

    @Test
    fun testInitialize_validUserId() {
        fragment = BookmarkUserFragment.newInstance("test")
        SupportFragmentTestUtil.startFragment(fragment, DriverActivity::class.java)

        assertThat(fragment.arguments.getString("ARG_USER_ID"), `is`("test"))
        assertThat(fragment.arguments.getBoolean("ARG_OWNER_FLAG"), `is`(false))
        assertThat(fragment.arguments.getInt("ARG_PAGE_INDEX"), `is`(0))
    }

    @Test
    fun testInitialize_invalidUserId() {
        thrown.expect(IllegalArgumentException::class.java)
        fragment = BookmarkUserFragment.newInstance("")
        SupportFragmentTestUtil.startFragment(fragment, DriverActivity::class.java)
    }
}
