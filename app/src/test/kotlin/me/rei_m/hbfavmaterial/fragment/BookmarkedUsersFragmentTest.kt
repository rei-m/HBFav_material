package me.rei_m.hbfavmaterial.fragment

import android.os.Build
import me.rei_m.hbfavmaterial.BuildConfig
import me.rei_m.hbfavmaterial.TestApp
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
        application = TestApp::class,
        sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP))
class BookmarkedUsersFragmentTest {


}