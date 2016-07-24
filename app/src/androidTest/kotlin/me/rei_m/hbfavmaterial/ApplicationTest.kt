package me.rei_m.hbfavmaterial

import android.app.Application
import android.support.test.InstrumentationRegistry
import android.test.ApplicationTestCase
import me.rei_m.hbfavmaterial.di.*
import org.junit.Before

/**
 * [Testing Fundamentals](http://d.android.com/tools/testing/testing_android.html)
 */
class ApplicationTest : ApplicationTestCase<Application>(Application::class.java) {

    companion object {
        // platformStatic allow access it from java code
        @JvmStatic lateinit var graph: ApplicationComponent
    }

    @Before
    override fun setUp() {
        super.setUp()
//        ApplicationTest.graph = DaggerApplicationComponent.builder()
//                .applicationModule(ApplicationModule(InstrumentationRegistry.getTargetContext().applicationContext as Application))
//                .appLayerModule(AppLayerModule())
//                .infraLayerModule(InfraLayerModule())
//                .build()
    }
}