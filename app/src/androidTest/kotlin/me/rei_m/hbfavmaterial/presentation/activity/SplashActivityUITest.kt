package me.rei_m.hbfavmaterial.presentation.activity

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.closeSoftKeyboard
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.WindowManager
import com.squareup.spoon.Spoon
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.*
import me.rei_m.hbfavmaterial.presentation.fragment.InitializeContact
import me.rei_m.hbfavmaterial.presentation.fragment.InitializeFragment
import me.rei_m.hbfavmaterial.presentation.manager.ActivityNavigator
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class SplashActivityUITest {

    @Mock
    lateinit var applicationComponent: ApplicationComponent

    @Mock
    lateinit var activityComponent: SplashActivityComponent

    @Mock
    lateinit var fragmentComponent: InitializeFragmentComponent

    @Mock
    lateinit var navigator: ActivityNavigator

    @Mock
    lateinit var presenter: InitializeContact.Actions

    @Rule
    @JvmField
    val activityRule: ActivityTestRule<SplashActivity> = object : ActivityTestRule<SplashActivity>(SplashActivity::class.java) {

        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()

            MockitoAnnotations.initMocks(this@SplashActivityUITest)

            `when`(applicationComponent.plus(any(SplashActivityModule::class.java))).thenReturn(activityComponent)

            `when`(activityComponent.plus(any(InitializeFragmentModule::class.java))).thenReturn(fragmentComponent)

            doAnswer {
                val fragment: InitializeFragment = it.arguments[0] as InitializeFragment
                fragment.presenter = presenter
                fragment.navigator = navigator
                return@doAnswer fragment
            }.`when`(fragmentComponent).inject(any(InitializeFragment::class.java))

            val app = InstrumentationRegistry.getTargetContext().applicationContext as App

            app.component = applicationComponent
        }
    }

    val fragment: InitializeFragment
        get() = activityRule.activity.supportFragmentManager.findFragmentByTag(InitializeFragment::class.java.simpleName) as InitializeFragment

    @Before
    fun setUp() {
        val activity = activityRule.activity
        val wakeUpDevice = Runnable {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)
        }
        activity.runOnUiThread(wakeUpDevice)
    }

    @Test
    fun testInitializedView() {

        Spoon.screenshot(activityRule.activity, "initial_state")

        // はてなID入力欄が表示.
        onView(withId(R.id.fragment_initialize_edit_hatena_id))
                .perform(closeSoftKeyboard(), scrollTo())
                .check(matches(isDisplayed()))

        // はてなID送信ボタンが無効な状態で表示.
        onView(withId(R.id.fragment_initialize_button_set_hatena_id))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .check(matches(not(isEnabled())))

        // FABは表示されてない.
        onView(withId(R.id.fab))
                .check(matches(not(isDisplayed())))
    }
}
