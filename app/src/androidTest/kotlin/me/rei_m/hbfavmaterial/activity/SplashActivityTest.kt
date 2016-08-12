package me.rei_m.hbfavmaterial.activity

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import dagger.Component
import dagger.Module
import dagger.Subcomponent
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.*
import me.rei_m.hbfavmaterial.fragment.InitializeFragment
import me.rei_m.hbfavmaterial.fragment.presenter.InitializeContact
import org.hamcrest.Matchers.not
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

    @Test
    fun FABが表示されていない() {
        onView(withId(R.id.fab)).check(matches(not(isDisplayed())))
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
                override fun onCreate(component: FragmentComponent, view: InitializeContact.View) {

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
