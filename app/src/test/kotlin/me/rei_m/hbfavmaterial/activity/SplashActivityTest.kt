package me.rei_m.hbfavmaterial.activity

import android.support.design.widget.FloatingActionButton
import android.view.View
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.fragment.InitializeFragment
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SplashActivityTest {

    private lateinit var splashActivity: SplashActivity

    private val fragment: InitializeFragment
        get() = splashActivity.supportFragmentManager.findFragmentByTag(InitializeFragment::class.java.simpleName) as InitializeFragment

    private val fab: FloatingActionButton
        get() = splashActivity.findViewById(R.id.fab) as FloatingActionButton

    @Before
    fun setUp() {

        // 実行前に差し替えたい場合の例
//        val testApp = RuntimeEnvironment.application as App
//        val appComponent = DaggerTestApplicationComponent.builder()
//                .applicationModule(ApplicationModule(testApp))
//                .infraLayerModule(InfraLayerModule())
//                .build()
//        testApp.component = appComponent

        splashActivity = Robolectric.setupActivity(SplashActivity::class.java)
    }

    @Test
    fun initialize() {
        assertNotNull(fragment)
        assertThat(fab.visibility, `is`(View.GONE))
    }
}
