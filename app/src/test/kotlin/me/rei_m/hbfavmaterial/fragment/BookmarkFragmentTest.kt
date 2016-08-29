package me.rei_m.hbfavmaterial.fragment

import android.view.View
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.testutil.DriverActivity
import me.rei_m.hbfavmaterial.testutil.TestUtil
import me.rei_m.hbfavmaterial.view.widget.bookmark.BookmarkContentsLayout
import me.rei_m.hbfavmaterial.view.widget.bookmark.BookmarkCountTextView
import me.rei_m.hbfavmaterial.view.widget.bookmark.BookmarkHeaderLayout
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil

@RunWith(RobolectricTestRunner::class)
class BookmarkFragmentTest {

    lateinit var fragment: BookmarkFragment

    private val activity: CustomDriverActivity
        get() = fragment.activity as CustomDriverActivity

    private val view: View by lazy {
        fragment.view ?: throw IllegalStateException("fragment's view is Null")
    }

    private val bookmarkEntity = TestUtil.createTestBookmarkEntity(0)

    private val bookmarkHeaderLayout: BookmarkHeaderLayout
        get() = view.findViewById(R.id.fragment_bookmark_layout_header) as BookmarkHeaderLayout

    private val bookmarkContents: BookmarkContentsLayout
        get() = view.findViewById(R.id.layout_bookmark_contents) as BookmarkContentsLayout

    private val bookmarkCountTextView: BookmarkCountTextView
        get() = view.findViewById(R.id.fragment_bookmark_text_bookmark_count) as BookmarkCountTextView

    @Before
    fun setUp() {

        fragment = BookmarkFragment.newInstance(bookmarkEntity)

        SupportFragmentTestUtil.startFragment(fragment, CustomDriverActivity::class.java)
    }

    @Test
    fun testInitialize() {
        assertThat(bookmarkHeaderLayout.visibility, `is`(View.VISIBLE))
        assertThat(bookmarkContents.visibility, `is`(View.VISIBLE))
        assertThat(bookmarkCountTextView.visibility, `is`(View.VISIBLE))
    }

    @Test
    fun testInitialize_clickBookmarkHeaderLayout() {
        bookmarkHeaderLayout.performClick()
        assertThat(activity.isBookmarkUserClicked, `is`(true))
    }

    @Test
    fun testInitialize_clickBookmarkContents() {
        bookmarkContents.performClick()
        assertThat(activity.isBookmarkClicked, `is`(true))
    }

    @Test
    fun testInitialize_clickBookmarkCount() {
        bookmarkCountTextView.performClick()
        assertThat(activity.isBookmarkCountClicked, `is`(true))
    }

    class CustomDriverActivity : DriverActivity(),
            BookmarkFragment.OnFragmentInteractionListener {

        var isBookmarkUserClicked = false

        var isBookmarkClicked = false

        var isBookmarkCountClicked = false

        override fun onClickBookmarkUser(bookmarkEntity: BookmarkEntity) {
            isBookmarkUserClicked = true
        }

        override fun onClickBookmark(bookmarkEntity: BookmarkEntity) {
            isBookmarkClicked = true
        }

        override fun onClickBookmarkCount(bookmarkEntity: BookmarkEntity) {
            isBookmarkCountClicked = true
        }
    }
}