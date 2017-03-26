package me.rei_m.hbfavmaterial.presentation.fragment

import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.BookmarkFavoriteFragmentComponent
import me.rei_m.hbfavmaterial.di.BookmarkFavoriteFragmentModule
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.di.MainActivityComponent
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.presentation.view.adapter.BookmarkListAdapter
import me.rei_m.hbfavmaterial.testutil.DriverActivity
import me.rei_m.hbfavmaterial.testutil.TestUtil
import me.rei_m.hbfavmaterial.testutil.bindView
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil

@RunWith(RobolectricTestRunner::class)
class BookmarkFavoriteFragmentTest {

    lateinit var fragment: BookmarkFavoriteFragment

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

    @Mock
    lateinit var activityNavigator: ActivityNavigator

    @Mock
    lateinit var presenter: BookmarkFavoriteContact.Actions

    @Before
    fun setUp() {

        MockitoAnnotations.initMocks(this)

        fragment = BookmarkFavoriteFragment.newInstance(0)
        fragment.activityNavigator = activityNavigator
        fragment.presenter = presenter

        SupportFragmentTestUtil.startFragment(fragment, CustomDriverActivity::class.java)
    }

    @Test
    fun testInitialize() {
        assertThat(holder.listView.visibility, `is`(View.VISIBLE))
        assertThat(holder.layoutRefresh.visibility, `is`(View.VISIBLE))
        assertThat(holder.textEmpty.visibility, `is`(View.GONE))
        assertThat(holder.progressBar.visibility, `is`(View.GONE))
        assertThat(fragment.hasOptionsMenu(), `is`(false))
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
        fragment.navigateToBookmark(bookmarkEntity)
        verify(activityNavigator).navigateToBookmark(fragment.activity, bookmarkEntity)
    }

    class CustomDriverActivity : DriverActivity(),
            HasComponent<MainActivityComponent> {

        override fun getComponent(): MainActivityComponent {

            val activityComponent = mock(MainActivityComponent::class.java)

            `when`(activityComponent.plus(any(BookmarkFavoriteFragmentModule::class.java)))
                    .thenReturn(mock(BookmarkFavoriteFragmentComponent::class.java))

            return activityComponent
        }
    }

    class ViewHolder(view: View) {
        val listView by view.bindView<ListView>(R.id.fragment_list_list)
        val layoutRefresh by view.bindView<SwipeRefreshLayout>(R.id.fragment_list_refresh)
        val textEmpty by view.bindView<TextView>(R.id.fragment_list_view_empty)
        val progressBar by view.bindView<ProgressBar>(R.id.fragment_list_progress_list)
    }
}
