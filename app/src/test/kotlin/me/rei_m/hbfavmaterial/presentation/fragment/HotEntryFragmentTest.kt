package me.rei_m.hbfavmaterial.presentation.fragment

import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.di.HotEntryFragmentComponent
import me.rei_m.hbfavmaterial.di.HotEntryFragmentModule
import me.rei_m.hbfavmaterial.di.MainActivityComponent
import me.rei_m.hbfavmaterial.domain.entity.EntryEntity
import me.rei_m.hbfavmaterial.constant.EntryTypeFilter
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.presentation.view.adapter.EntryListAdapter
import me.rei_m.hbfavmaterial.testutil.DriverActivity
import me.rei_m.hbfavmaterial.testutil.TestUtil
import me.rei_m.hbfavmaterial.testutil.bindView
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.fakes.RoboMenuItem
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil

@RunWith(RobolectricTestRunner::class)
class HotEntryFragmentTest {

    lateinit var fragment: HotEntryFragment

    private val holder: ViewHolder by lazy {
        val view = fragment.view ?: throw IllegalStateException("fragment's view is Null")
        ViewHolder(view)
    }

    private val snackbarTextView: TextView
        get() = fragment.activity.findViewById(android.support.design.R.id.snackbar_text) as TextView

    @Mock
    lateinit var presenter: HotEntryContact.Actions

    @Mock
    lateinit var activityNavigator: ActivityNavigator

    @Before
    fun setUp() {

        MockitoAnnotations.initMocks(this)

        fragment = HotEntryFragment.newInstance(0)
        fragment.presenter = presenter
        fragment.activityNavigator = activityNavigator

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
    fun testShowHideEntryList() {
        val entryList = arrayListOf<EntryEntity>().apply {
            add(TestUtil.createTestEntryEntity(1))
            add(TestUtil.createTestEntryEntity(2))
            add(TestUtil.createTestEntryEntity(3))
            add(TestUtil.createTestEntryEntity(4))
        }

        holder.listView.visibility = View.GONE
        holder.layoutRefresh.isRefreshing = true
        assertThat(holder.listView.adapter.count, `is`(0))

        fragment.showEntryList(entryList)

        assertThat(holder.listView.visibility, `is`(View.VISIBLE))
        assertThat(holder.layoutRefresh.isRefreshing, `is`(false))

        val adapter = holder.listView.adapter as EntryListAdapter
        assertThat(adapter.count, `is`(4))
        assertThat(adapter.getItem(0), `is`(entryList[0]))
        assertThat(adapter.getItem(3), `is`(entryList[3]))

        fragment.hideEntryList()
        assertThat(holder.listView.visibility, `is`(View.GONE))
    }

    @Test
    fun testShowNetworkErrorMessage() {
        fragment.showNetworkErrorMessage()
        assertThat(snackbarTextView.visibility, `is`(View.VISIBLE))
        assertThat(snackbarTextView.text.toString(), `is`(fragment.getString(R.string.message_error_network)))
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
    fun testShowHideEmpty() {
        holder.textEmpty.visibility = View.GONE

        fragment.showEmpty()
        assertThat(holder.textEmpty.visibility, `is`(View.VISIBLE))

        fragment.hideEmpty()
        assertThat(holder.textEmpty.visibility, `is`(View.GONE))
    }

    @Test
    fun testNavigateToBookmark() {
        val entryEntity = TestUtil.createTestEntryEntity(1)
        fragment.navigateToBookmark(entryEntity)
        verify(activityNavigator).navigateToBookmark(fragment.activity, entryEntity)
    }

    @Test
    fun testOnOptionsItemSelected() {
        val menuItem = RoboMenuItem(R.id.fragment_entry_menu_category_animation_and_game)

        `when`(fragment.presenter.entryTypeFilter).thenReturn(EntryTypeFilter.ANIMATION_AND_GAME)

        fragment.onOptionsItemSelected(menuItem)
        verify(fragment.presenter).onOptionItemSelected(EntryTypeFilter.ANIMATION_AND_GAME)

        val activity = fragment.activity as CustomDriverActivity
        assertThat(activity.newPageTitle, `is`(fragment.pageTitle))
    }

    class CustomDriverActivity : DriverActivity(),
            HasComponent<MainActivityComponent>,
            HotEntryFragment.OnFragmentInteractionListener {

        var newPageTitle = ""

        override fun onChangeFilter(newPageTitle: String) {
            this.newPageTitle = newPageTitle
        }

        override fun getComponent(): MainActivityComponent {
            val activityComponent = mock(MainActivityComponent::class.java)

            `when`(activityComponent.plus(any(HotEntryFragmentModule::class.java)))
                    .thenReturn(mock(HotEntryFragmentComponent::class.java))

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
