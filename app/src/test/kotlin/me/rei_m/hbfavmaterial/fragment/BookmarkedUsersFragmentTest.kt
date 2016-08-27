package me.rei_m.hbfavmaterial.fragment

import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.enum.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.testutil.DriverActivity
import me.rei_m.hbfavmaterial.testutil.TestUtil
import me.rei_m.hbfavmaterial.view.adapter.UserListAdapter
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil

@RunWith(RobolectricTestRunner::class)
class BookmarkedUsersFragmentTest {

    lateinit var fragment: BookmarkedUsersFragment

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

    private val snackbarTextView: TextView
        get() = fragment.activity.findViewById(android.support.design.R.id.snackbar_text) as TextView

    private fun getString(resId: Int): String {
        return fragment.getString(resId)
    }

    private val bookmarkEntity = TestUtil.createTestBookmarkEntity(0)

    @Before
    fun setUp() {

        fragment = BookmarkedUsersFragment.newInstance(bookmarkEntity)

        SupportFragmentTestUtil.startFragment(fragment, CustomDriverActivity::class.java)
    }

    @Test
    fun testInitialize() {
        assertThat(listView.visibility, `is`(View.VISIBLE))
        assertThat(layoutRefresh.visibility, `is`(View.VISIBLE))
        assertThat(textEmpty.visibility, `is`(View.GONE))
        assertThat(fragment.hasOptionsMenu(), `is`(true))
    }

    @Test
    fun testShowUserList() {

        val bookmarkList = arrayListOf<BookmarkEntity>().apply {
            add(TestUtil.createTestBookmarkEntity(1))
            add(TestUtil.createTestBookmarkEntity(2))
            add(TestUtil.createTestBookmarkEntity(3))
            add(TestUtil.createTestBookmarkEntity(4))
        }

        layoutRefresh.isRefreshing = true
        fragment.showUserList(bookmarkList)

        val adapter = listView.adapter as UserListAdapter

        assertThat(listView.visibility, `is`(View.VISIBLE))
        assertThat(adapter.count, `is`(4))
        assertThat(adapter.getItem(0), `is`(bookmarkList[0]))
        assertThat(adapter.getItem(3), `is`(bookmarkList[3]))

        assertThat(layoutRefresh.isRefreshing, `is`(false))

        val activity = fragment.activity as CustomDriverActivity
        assertThat(activity.bookmarkCommentFilter, `is`(fragment.presenter.bookmarkCommentFilter))
    }

    @Test
    fun testHideUserList() {
        fragment.hideUserList()
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
    fun testNavigateToOthersBookmark() {
        val navigator = spy(fragment.activityNavigator)
        doAnswer { Unit }.`when`(navigator).navigateToOthersBookmark(fragment.activity, bookmarkEntity.creator)
        fragment.activityNavigator = navigator
        fragment.navigateToOthersBookmark(bookmarkEntity)
        verify(navigator).navigateToOthersBookmark(fragment.activity, bookmarkEntity.creator)
    }

    class CustomDriverActivity : DriverActivity(),
            BookmarkedUsersFragment.OnFragmentInteractionListener {

        var bookmarkCommentFilter: BookmarkCommentFilter? = null

        override fun onChangeFilter(bookmarkCommentFilter: BookmarkCommentFilter) {
            this.bookmarkCommentFilter = bookmarkCommentFilter
        }
    }
}
