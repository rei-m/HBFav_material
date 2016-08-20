package me.rei_m.hbfavmaterial.activity

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.WindowManager
import dagger.Component
import dagger.Module
import dagger.Subcomponent
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.*
import me.rei_m.hbfavmaterial.fragment.InitializeFragment
import me.rei_m.hbfavmaterial.fragment.presenter.InitializeContact
import me.rei_m.hbfavmaterial.repository.UserRepository
import me.rei_m.hbfavmaterial.service.UserService
import me.rei_m.hbfavmaterial.util.CustomMatcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import javax.inject.Singleton

@RunWith(AndroidJUnit4::class)
class SplashActivityTest {

    @Rule
    @JvmField
    val activityRule: ActivityTestRule<SplashActivity> = object : ActivityTestRule<SplashActivity>(SplashActivity::class.java) {

        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()

            val app = InstrumentationRegistry.getTargetContext().applicationContext as App

            val appComponent = DaggerSplashActivityTest_TestApplicationComponent.builder()
                    .applicationModule(ApplicationModule(app))
                    .infraLayerModule(InfraLayerModule())
                    .build()

            app.component = appComponent
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
    fun 初期表示の確認() {

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

    @Test
    fun はてなIDを入力すると送信ボタンが有効になる() {

        onView(withId(R.id.fragment_initialize_edit_hatena_id))
                .perform(scrollTo(), typeText("a"))

        onView(withId(R.id.fragment_initialize_button_set_hatena_id))
                .perform(scrollTo())
                .check(matches(isEnabled()))
    }

    @Test
    fun 入力したはてなIDを消すと送信ボタンが無効になる() {

        onView(withId(R.id.fragment_initialize_edit_hatena_id))
                .perform(scrollTo(), typeText("a"))
        onView(withId(R.id.fragment_initialize_edit_hatena_id))
                .perform(replaceText(""))
        onView(withId(R.id.fragment_initialize_button_set_hatena_id))
                .perform(scrollTo())
                .check(matches(not(isEnabled())))
    }

    @Test
    fun 送信ボタンをクリックしたイベントがPresenterに伝わる() {

        val presenter = mock(InitializeContact.Actions::class.java)
        doAnswer { Unit }.`when`(presenter).onClickButtonSetId("valid")
        fragment.presenter = presenter

        onView(withId(R.id.fragment_initialize_edit_hatena_id))
                .perform(scrollTo(), typeText("valid"))
        onView(withId(R.id.fragment_initialize_button_set_hatena_id))
                .perform(scrollTo(), closeSoftKeyboard(), click())

        verify(presenter, times(1)).onClickButtonSetId("valid")
    }

    @Test
    fun testShowNetworkErrorMessage() {

        onView(withId(R.id.fragment_initialize_edit_hatena_id))
                .perform(scrollTo(), typeText("errore"))

        onView(withId(R.id.fragment_initialize_button_set_hatena_id))
                .perform(scrollTo(), closeSoftKeyboard(), click())

        activityRule.activity.runOnUiThread {
            fragment.showNetworkErrorMessage()
        }

        onView(allOf(withId(android.support.design.R.id.snackbar_text), withText(R.string.message_error_network)))
                .check(matches(isEnabled()))
    }

    @Test
    fun testDisplayInvalidUserIdMessage() {

        onView(withId(R.id.fragment_initialize_edit_hatena_id))
                .perform(scrollTo(), typeText("error"))

        activityRule.activity.runOnUiThread {
            fragment.displayInvalidUserIdMessage()
        }

        onView(withId(R.id.fragment_initialize_button_set_hatena_id))
                .perform(scrollTo(), closeSoftKeyboard(), click())

        onView(withId(R.id.fragment_initialize_layout_hatena_id))
                .perform(scrollTo())
                .check(matches(CustomMatcher.withErrorText(R.string.message_error_input_user_id)))
    }

    @Test
    fun testShowProgress() {
        activityRule.activity.runOnUiThread {
            fragment.showProgress()
        }
        onView(withText(R.string.text_progress_loading)).check(matches(isDisplayed()))
    }

    @Test
    fun testHideProgress() {
        activityRule.activity.runOnUiThread {
            fragment.showProgress()
            fragment.hideProgress()
        }
        onView(withText(R.string.text_progress_loading)).check(doesNotExist())
    }


    @Test
    fun testNavigateToMain() {
        val navigator = spy(fragment.navigator)
        doAnswer { Unit }.`when`(navigator).navigateToMain(activityRule.activity)
        fragment.navigator = navigator
        fragment.navigateToMain()
        verify(navigator, times(1)).navigateToMain(activityRule.activity)
    }

    @Singleton
    @Component(modules = arrayOf(ApplicationModule::class, InfraLayerModule::class))
    interface TestApplicationComponent : ApplicationComponent {
        override fun activityComponent(): TestActivityComponent
    }

    @Subcomponent(modules = arrayOf(ActivityModule::class))
    interface TestActivityComponent : ActivityComponent {
        override fun fragmentComponent(): TestFragmentComponent
    }

    @Subcomponent(modules = arrayOf(TestInitializeFragmentModule::class))
    interface TestFragmentComponent : FragmentComponent {
        override fun inject(fragment: InitializeFragment)
    }

    @Module
    class TestInitializeFragmentModule() : FragmentModule() {

        override fun createInitializePresenter(context: Context,
                                               userRepository: UserRepository,
                                               userService: UserService): InitializeContact.Actions {
            return object : InitializeContact.Actions {
                override fun onCreate(view: InitializeContact.View) {
                }

                override fun onResume() {
                }

                override fun onPause() {
                }

                override fun onClickButtonSetId(userId: String) {
                }
            }
        }
    }
}
