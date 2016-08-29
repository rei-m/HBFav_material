package me.rei_m.hbfavmaterial.fragment

import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.testutil.DriverActivity
import me.rei_m.hbfavmaterial.testutil.TestUtil
import me.rei_m.hbfavmaterial.testutil.bindView
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil

@RunWith(RobolectricTestRunner::class)
class BookmarkUserFragmentOwnerTest {

    lateinit var fragment: BookmarkUserFragment

    private val activity: CustomDriverActivity
        get() = fragment.activity as CustomDriverActivity

    private val holder: ViewHolder by lazy {
        val view = fragment.view ?: throw IllegalStateException("fragment's view is Null")
        ViewHolder(view)
    }

    @Before
    fun setUp() {
        fragment = BookmarkUserFragment.newInstance(1)

        SupportFragmentTestUtil.startFragment(fragment, CustomDriverActivity::class.java)
    }

    @Test
    fun testInitialize() {
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
    fun testShowBookmarkList() {

        val bookmarkList = mutableListOf<BookmarkEntity>()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(0))
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1))

        holder.listView.visibility = View.GONE
        assertThat(holder.listView.visibility, `is`(View.GONE))

        fragment.showBookmarkList(bookmarkList)
        assertThat(holder.listView.visibility, `is`(View.VISIBLE))

        fragment.hideBookmarkList()
        assertThat(holder.listView.visibility, `is`(View.GONE))
    }

    @Test
    fun testShowNetworkErrorMessage() {

    }

    @Test
    fun testShowProgress() {

    }

    @Test
    fun testHideProgress() {

    }

    @Test
    fun testStartAutoLoading() {

    }

    @Test
    fun testStopAutoLoading() {

    }

    @Test
    fun testShowEmpty() {

    }

    @Test
    fun testHideEmpty() {

    }

    @Test
    fun testNavigateToBookmark() {

    }

    class ViewHolder(view: View) {
        val listView by view.bindView<ListView>(R.id.fragment_list_list)
        val layoutRefresh by view.bindView<SwipeRefreshLayout>(R.id.fragment_list_refresh)
        val textEmpty by view.bindView<TextView>(R.id.fragment_list_view_empty)
        val progressBar by view.bindView<ProgressBar>(R.id.fragment_list_progress_list)
    }

    class CustomDriverActivity : DriverActivity(),
            BookmarkUserFragment.OnFragmentInteractionListener {

        override fun onChangeFilter(newPageTitle: String) {

        }
    }
}
