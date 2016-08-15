package me.rei_m.hbfavmaterial.activity

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
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
import me.rei_m.hbfavmaterial.util.CustomMatcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
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
        onView(withId(R.id.fragment_initialize_edit_hatena_id)).check(matches(isDisplayed()))

        // はてなID送信ボタンが無効な状態で表示.
        onView(withId(R.id.fragment_initialize_button_set_hatena_id)).check(matches(isDisplayed()))
        onView(withId(R.id.fragment_initialize_button_set_hatena_id)).check(matches(not(isEnabled())))

        // FABは表示されてない.
        onView(withId(R.id.fab)).check(matches(not(isDisplayed())))
    }

    @Test
    fun はてなIDを入力すると送信ボタンが有効になる() {
        onView(withId(R.id.fragment_initialize_edit_hatena_id)).perform(typeText("a"))
        onView(withId(R.id.fragment_initialize_button_set_hatena_id)).check(matches(isEnabled()))
    }

    @Test
    fun 入力したはてなIDを消すと送信ボタンが無効になる() {
        onView(withId(R.id.fragment_initialize_edit_hatena_id)).perform(typeText("a"))
        onView(withId(R.id.fragment_initialize_edit_hatena_id)).perform(replaceText(""))
        onView(withId(R.id.fragment_initialize_button_set_hatena_id)).check(matches(not(isEnabled())))
    }

    @Test
    fun 正しいはてなIDが入力されるとメイン画面に行く() {
        onView(withId(R.id.fragment_initialize_edit_hatena_id)).perform(typeText("valid"))
        onView(withId(R.id.fragment_initialize_button_set_hatena_id))
                .perform(closeSoftKeyboard())
                .perform(click())

        // メイン画面のツールバーが表示されたら遷移したとみなす.
        onView(withId(R.id.app_bar_main_toolbar)).check(matches(isDisplayed()))
    }

    @Test
    fun 誤ったはてなIDが入力されるとエラーメッセージが表示される() {
        onView(withId(R.id.fragment_initialize_edit_hatena_id)).perform(typeText("invalid"))
        onView(withId(R.id.fragment_initialize_button_set_hatena_id))
                .perform(closeSoftKeyboard())
                .perform(click())
        onView(withId(R.id.fragment_initialize_layout_hatena_id))
                .check(matches(CustomMatcher.withErrorText(R.string.message_error_input_user_id)))
    }

    @Test
    fun ネットワークエラーの時はSnackbarが表示される() {
        onView(withId(R.id.fragment_initialize_edit_hatena_id)).perform(typeText("error"))
        onView(withId(R.id.fragment_initialize_button_set_hatena_id))
                .perform(closeSoftKeyboard())
                .perform(click())

        onView(allOf(withId(android.support.design.R.id.snackbar_text), withText(R.string.message_error_network)))
                .check(matches(isDisplayed()))
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

        override fun createInitializePresenter(context: Context): InitializeContact.Actions {
            return object : InitializeContact.Actions {

                override lateinit var view: InitializeContact.View

                override fun onCreate(component: FragmentComponent, view: InitializeContact.View) {
                    this.view = view
                }

                override fun onResume() {

                }

                override fun onPause() {

                }

                override fun onClickButtonSetId(userId: String) {
                    if (userId == "valid") {
                        view.navigateToMain()
                    } else if (userId == "invalid") {
                        view.displayInvalidUserIdMessage()
                    } else {
                        view.showNetworkErrorMessage()
                    }
                }
            }
        }
    }
}
