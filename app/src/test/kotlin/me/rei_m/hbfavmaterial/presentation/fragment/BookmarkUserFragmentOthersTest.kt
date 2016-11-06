package me.rei_m.hbfavmaterial.presentation.fragment

import me.rei_m.hbfavmaterial.di.BookmarkUserFragmentComponent
import me.rei_m.hbfavmaterial.di.BookmarkUserFragmentModule
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.di.MainActivityComponent
import me.rei_m.hbfavmaterial.presentation.manager.ActivityNavigator
import me.rei_m.hbfavmaterial.testutil.DriverActivity
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil

@RunWith(RobolectricTestRunner::class)
class BookmarkUserFragmentOthersTest {

    lateinit var fragment: BookmarkUserFragment

    @Rule
    @JvmField
    val thrown: ExpectedException? = ExpectedException.none()

    @Mock
    lateinit var activityNavigator: ActivityNavigator

    @Mock
    lateinit var presenter: BookmarkUserContact.Actions

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testInitialize_validUserId() {
        fragment = BookmarkUserFragment.newInstance("test")
        fragment.activityNavigator = activityNavigator
        fragment.presenter = presenter

        SupportFragmentTestUtil.startFragment(fragment, CustomDriverActivity::class.java)

        assertThat(fragment.arguments.getString("ARG_USER_ID"), `is`("test"))
        assertThat(fragment.arguments.getBoolean("ARG_OWNER_FLAG"), `is`(false))
        assertThat(fragment.arguments.getInt("ARG_PAGE_INDEX"), `is`(0))
    }

    @Test
    fun testInitialize_invalidUserId() {
        thrown?.expect(IllegalArgumentException::class.java)
        fragment = BookmarkUserFragment.newInstance("")
        fragment.activityNavigator = activityNavigator
        fragment.presenter = presenter

        SupportFragmentTestUtil.startFragment(fragment, CustomDriverActivity::class.java)
    }

    class CustomDriverActivity : DriverActivity(),
            HasComponent<MainActivityComponent> {

        override fun getComponent(): MainActivityComponent {

            val activityComponent = mock(MainActivityComponent::class.java)

            `when`(activityComponent.plus(any(BookmarkUserFragmentModule::class.java)))
                    .thenReturn(mock(BookmarkUserFragmentComponent::class.java))

            return activityComponent
        }
    }
}
