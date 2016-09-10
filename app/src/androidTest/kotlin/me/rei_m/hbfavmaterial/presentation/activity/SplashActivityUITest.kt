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
import dagger.Component
import dagger.Module
import dagger.Subcomponent
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.*
import me.rei_m.hbfavmaterial.presentation.fragment.InitializeContact
import me.rei_m.hbfavmaterial.presentation.fragment.InitializeFragment
import me.rei_m.hbfavmaterial.usecase.ConfirmExistingUserIdUsecase
import me.rei_m.hbfavmaterial.usecase.GetUserUsecase
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import javax.inject.Singleton

@RunWith(AndroidJUnit4::class)
class SplashActivityUITest {

    @Rule
    @JvmField
    val activityRule: ActivityTestRule<SplashActivity> = object : ActivityTestRule<SplashActivity>(SplashActivity::class.java) {

        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()

            val app = InstrumentationRegistry.getTargetContext().applicationContext as App

            val appComponent = DaggerSplashActivityUITest_TestApplicationComponent.builder()
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

        override fun createInitializePresenter(getUserUsecase: GetUserUsecase,
                                               confirmExistingUserIdUsecase: ConfirmExistingUserIdUsecase): InitializeContact.Actions {
            return mock(InitializeContact.Actions::class.java)
        }
    }
}
