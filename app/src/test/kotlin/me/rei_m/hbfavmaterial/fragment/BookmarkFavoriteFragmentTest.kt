package me.rei_m.hbfavmaterial.fragment

import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.testutil.TestUtil
import me.rei_m.hbfavmaterial.view.adapter.BookmarkListAdapter
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil

@RunWith(RobolectricTestRunner::class)
class BookmarkFavoriteFragmentTest {

    lateinit var fragment: BookmarkFavoriteFragment

    private val view: View by lazy {
        fragment.view ?: throw IllegalStateException("fragment's view is Null")
    }

    private val listView: ListView
        get() = view.findViewById(R.id.fragment_list_list) as ListView

    private val layoutRefresh: SwipeRefreshLayout
        get() = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout

    private val textEmpty: TextView
        get() = view.findViewById(R.id.fragment_list_view_empty) as TextView

    private val progressBar: ProgressBar
        get() = view.findViewById(R.id.fragment_list_progress_list) as ProgressBar

    private val viewListFooter: View
        get() = view.findViewById(R.id.list_footer_loading_layout)

    private val snackbarTextView: TextView
        get() = fragment.activity.findViewById(android.support.design.R.id.snackbar_text) as TextView

    private fun getString(resId: Int): String {
        return fragment.getString(resId)
    }

    @Before
    fun setUp() {

        fragment = BookmarkFavoriteFragment.newInstance(0)

        SupportFragmentTestUtil.startFragment(fragment, BookmarkedUsersFragmentTest.CustomDriverActivity::class.java)
    }

    @Test
    fun initialize() {
        assertThat(listView.visibility, `is`(View.VISIBLE))
        assertThat(layoutRefresh.visibility, `is`(View.VISIBLE))
        assertThat(textEmpty.visibility, `is`(View.GONE))
        assertThat(progressBar.visibility, `is`(View.GONE))
        assertThat(fragment.hasOptionsMenu(), `is`(false))
    }

    @Test
    fun testShowBookmarkList() {

        val bookmarkList = arrayListOf<BookmarkEntity>().apply {
            add(TestUtil.createTestBookmarkEntity(1))
            add(TestUtil.createTestBookmarkEntity(2))
            add(TestUtil.createTestBookmarkEntity(3))
            add(TestUtil.createTestBookmarkEntity(4))
        }

        fragment.showBookmarkList(bookmarkList)

        assertThat(listView.visibility, `is`(View.VISIBLE))
        assertThat(layoutRefresh.isRefreshing, `is`(false))

        val adapter = listView.adapter as BookmarkListAdapter
        assertThat(adapter.count, `is`(4))
        assertThat(adapter.getItem(0), `is`(bookmarkList[0]))
        assertThat(adapter.getItem(3), `is`(bookmarkList[3]))
    }

    @Test
    fun testHideBookmarkList() {
        fragment.hideBookmarkList()
        assertThat(listView.visibility, `is`(View.GONE))
    }

    @Test
    fun testShowNetworkErrorMessage() {
        fragment.showNetworkErrorMessage()
        assertThat(snackbarTextView.visibility, `is`(View.VISIBLE))
        assertThat(snackbarTextView.text.toString(), `is`(getString(R.string.message_error_network)))
    }

    @Test
    fun testShowProgress() {
        fragment.showProgress()
        assertThat(progressBar.visibility, `is`(View.VISIBLE))
    }

    @Test
    fun testHideProgress() {
        fragment.hideProgress()
        assertThat(progressBar.visibility, `is`(View.GONE))
    }

    @Test
    fun testStartAutoLoading() {
        fragment.startAutoLoading()
        assertThat(viewListFooter.visibility, `is`(View.VISIBLE))
        assertThat(listView.footerViewsCount, `is`(1))

        fragment.startAutoLoading()
        assertThat(listView.footerViewsCount, `is`(1))
    }

    @Test
    fun testStopAutoLoading() {
        fragment.stopAutoLoading()
        assertThat(listView.footerViewsCount, `is`(0))

        fragment.stopAutoLoading()
    }

    @Test
    fun testShowEmpty() {
        fragment.showEmpty()
        assertThat(textEmpty.visibility, `is`(View.VISIBLE))
    }

    @Test
    fun testHideEmpty() {
        fragment.hideEmpty()
        assertThat(textEmpty.visibility, `is`(View.GONE))
    }

    @Test
    fun testNavigateToBookmark() {
        val bookmarkEntity = TestUtil.createTestBookmarkEntity(1)
        val navigator = spy(fragment.activityNavigator)
        fragment.activityNavigator = navigator
        fragment.navigateToBookmark(bookmarkEntity)
        verify(navigator).navigateToBookmark(fragment.activity, bookmarkEntity)
    }
}
