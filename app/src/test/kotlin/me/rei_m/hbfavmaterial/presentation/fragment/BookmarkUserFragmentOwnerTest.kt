package me.rei_m.hbfavmaterial.presentation.fragment

import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.enum.ReadAfterFilter
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkUserContact
import me.rei_m.hbfavmaterial.testutil.DriverActivity
import me.rei_m.hbfavmaterial.testutil.TestUtil
import me.rei_m.hbfavmaterial.testutil.bindView
import me.rei_m.hbfavmaterial.presentation.view.adapter.BookmarkListAdapter
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.fakes.RoboMenuItem
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil

@RunWith(RobolectricTestRunner::class)
class BookmarkUserFragmentOwnerTest {

    lateinit var fragment: BookmarkUserFragment

    private val holder: ViewHolder by lazy {
        val view = fragment.view ?: throw IllegalStateException("fragment's view is Null")
        ViewHolder(view)
    }

    private val viewListFooter: View
        get() = fragment.view!!.findViewById(R.id.list_footer_loading_layout)

    private val snackbarTextView: TextView
        get() = fragment.activity.findViewById(android.support.design.R.id.snackbar_text) as TextView

    private fun getString(resId: Int): String {
        return fragment.getString(resId)
    }

    @Before
    fun setUp() {
        fragment = BookmarkUserFragment.newInstance(1)

        SupportFragmentTestUtil.startFragment(fragment, CustomDriverActivity::class.java)
    }

    @Test
    fun testInitialize() {

        assertThat(fragment.arguments.getString("ARG_USER_ID"), `is`(nullValue()))
        assertThat(fragment.arguments.getBoolean("ARG_OWNER_FLAG"), `is`(true))
        assertThat(fragment.arguments.getInt("ARG_PAGE_INDEX"), `is`(1))

        assertThat(holder.listView.visibility, `is`(View.VISIBLE))
        assertThat(holder.layoutRefresh.visibility, `is`(View.VISIBLE))
        assertThat(holder.textEmpty.visibility, `is`(View.GONE))
        assertThat(holder.progressBar.visibility, `is`(View.GONE))
        assertThat(fragment.hasOptionsMenu(), `is`(true))
    }

    @Test
    fun testGetPageIndex() {
        assertThat(fragment.pageIndex, `is`(1))
    }

    @Test
    fun testGetPageTitle() {
        val title = fragment.pageTitle
        assertThat(title, `is`("ブックマーク - 全て"))
    }

    @Test
    fun testShowHideBookmarkList() {

        val bookmarkList = arrayListOf<BookmarkEntity>().apply {
            add(TestUtil.createTestBookmarkEntity(1))
            add(TestUtil.createTestBookmarkEntity(2))
            add(TestUtil.createTestBookmarkEntity(3))
            add(TestUtil.createTestBookmarkEntity(4))
        }

        holder.listView.visibility = View.GONE
        holder.layoutRefresh.isRefreshing = true
        assertThat(holder.listView.adapter.count, `is`(0))

        fragment.showBookmarkList(bookmarkList)

        assertThat(holder.listView.visibility, `is`(View.VISIBLE))
        assertThat(holder.layoutRefresh.isRefreshing, `is`(false))

        val adapter = holder.listView.adapter as BookmarkListAdapter
        assertThat(adapter.count, `is`(4))
        assertThat(adapter.getItem(0), `is`(bookmarkList[0]))
        assertThat(adapter.getItem(3), `is`(bookmarkList[3]))

        fragment.hideBookmarkList()
        assertThat(holder.listView.visibility, `is`(View.GONE))
    }

    @Test
    fun testShowNetworkErrorMessage() {
        fragment.showNetworkErrorMessage()
        assertThat(snackbarTextView.visibility, `is`(View.VISIBLE))
        assertThat(snackbarTextView.text.toString(), `is`(getString(R.string.message_error_network)))
    }

    @Test
    fun testShowHideProgress() {

        holder.progressBar.visibility = View.GONE

        fragment.showProgress()
        assertThat(holder.progressBar.visibility, `is`(View.VISIBLE))

        fragment.hideProgress()
        assertThat(holder.progressBar.visibility, `is`(View.GONE))
    }

    @Test
    fun testStartStopAutoLoading() {

        assertThat(holder.listView.footerViewsCount, `is`(0))

        fragment.startAutoLoading()
        assertThat(viewListFooter.visibility, `is`(View.VISIBLE))
        assertThat(holder.listView.footerViewsCount, `is`(1))

        fragment.startAutoLoading()
        assertThat(holder.listView.footerViewsCount, `is`(1))

        fragment.stopAutoLoading()
        assertThat(holder.listView.footerViewsCount, `is`(0))

        fragment.stopAutoLoading()
        assertThat(holder.listView.footerViewsCount, `is`(0))
    }

    @Test
    fun testShowHideEmpty() {

        holder.textEmpty.visibility = View.GONE

        fragment.showEmpty()
        assertThat(holder.textEmpty.visibility, `is`(View.VISIBLE))

        fragment.hideEmpty()
        assertThat(holder.textEmpty.visibility, `is`(View.GONE))
    }

    @Test
    fun testNavigateToBookmark() {
        val bookmarkEntity = TestUtil.createTestBookmarkEntity(1)
        val navigator = spy(fragment.activityNavigator)
        fragment.activityNavigator = navigator
        fragment.navigateToBookmark(bookmarkEntity)
        verify(navigator).navigateToBookmark(fragment.activity, bookmarkEntity)
    }

    @Test
    fun testOnOptionsItemSelected() {
        val menuItem = RoboMenuItem(R.id.fragment_bookmark_user_menu_filter_bookmark_read_after)

        val presenter = mock(BookmarkUserContact.Actions::class.java)
        `when`(presenter.readAfterFilter).thenReturn(ReadAfterFilter.AFTER_READ)
        fragment.presenter = presenter

        fragment.onOptionsItemSelected(menuItem)
        verify(presenter).onOptionItemSelected(ReadAfterFilter.AFTER_READ)

        val activity = fragment.activity as CustomDriverActivity
        assertThat(activity.newPageTitle, `is`("ブックマーク - あとで読む"))
    }

    @Test
    fun testOnSaveInstanceState() {
        fragment.presenter.readAfterFilter = ReadAfterFilter.AFTER_READ
        val activity = fragment.activity
        activity.recreate()
        val recreatedFragment = activity.supportFragmentManager.fragments[0] as BookmarkUserFragment
        assertNotSame(fragment, recreatedFragment)
        assertThat(recreatedFragment.presenter.readAfterFilter, `is`(ReadAfterFilter.AFTER_READ))
    }

    class ViewHolder(view: View) {
        val listView by view.bindView<ListView>(R.id.fragment_list_list)
        val layoutRefresh by view.bindView<SwipeRefreshLayout>(R.id.fragment_list_refresh)
        val textEmpty by view.bindView<TextView>(R.id.fragment_list_view_empty)
        val progressBar by view.bindView<ProgressBar>(R.id.fragment_list_progress_list)
    }

    class CustomDriverActivity : DriverActivity(),
            BookmarkUserFragment.OnFragmentInteractionListener {

        var newPageTitle: String = ""

        override fun onChangeFilter(newPageTitle: String) {
            this.newPageTitle = newPageTitle
        }
    }
}
