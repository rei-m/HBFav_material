package me.rei_m.hbfavmaterial.presentation.fragment

import android.view.View
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.testutil.DriverActivity
import me.rei_m.hbfavmaterial.testutil.TestUtil
import me.rei_m.hbfavmaterial.testutil.bindView
import me.rei_m.hbfavmaterial.presentation.view.widget.bookmark.BookmarkContentsLayout
import me.rei_m.hbfavmaterial.presentation.view.widget.bookmark.BookmarkCountTextView
import me.rei_m.hbfavmaterial.presentation.view.widget.bookmark.BookmarkHeaderLayout
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

    private val holder: ViewHolder by lazy {
        val view = fragment.view ?: throw IllegalStateException("fragment's view is Null")
        ViewHolder(view)
    }

    private val bookmarkEntity = TestUtil.createTestBookmarkEntity(0)

    @Before
    fun setUp() {

        fragment = BookmarkFragment.newInstance(bookmarkEntity)

        SupportFragmentTestUtil.startFragment(fragment, CustomDriverActivity::class.java)
    }

    @Test
    fun testInitialize() {
        assertThat(holder.bookmarkHeaderLayout.visibility, `is`(View.VISIBLE))
        assertThat(holder.bookmarkContents.visibility, `is`(View.VISIBLE))
        assertThat(holder.bookmarkCountTextView.visibility, `is`(View.VISIBLE))
    }

    @Test
    fun testInitialize_clickBookmarkHeaderLayout() {
        holder.bookmarkHeaderLayout.performClick()
        assertThat(activity.isBookmarkUserClicked, `is`(true))
    }

    @Test
    fun testInitialize_clickBookmarkContents() {
        holder.bookmarkContents.performClick()
        assertThat(activity.isBookmarkClicked, `is`(true))
    }

    @Test
    fun testInitialize_clickBookmarkCount() {
        holder.bookmarkCountTextView.performClick()
        assertThat(activity.isBookmarkCountClicked, `is`(true))
    }

    class ViewHolder(view: View) {

        val bookmarkHeaderLayout by view.bindView<BookmarkHeaderLayout>(R.id.fragment_bookmark_layout_header)

        val bookmarkContents by view.bindView<BookmarkContentsLayout>(R.id.layout_bookmark_contents)

        val bookmarkCountTextView by view.bindView<BookmarkCountTextView>(R.id.fragment_bookmark_text_bookmark_count)
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