package me.rei_m.hbfavmaterial.presentation.fragment

import me.rei_m.hbfavmaterial.domain.entity.EntryEntity
import me.rei_m.hbfavmaterial.enum.EntryTypeFilter
import me.rei_m.hbfavmaterial.testutil.TestUtil
import me.rei_m.hbfavmaterial.usecase.GetHotEntriesUsecase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.runners.MockitoJUnitRunner
import rx.Observable
import rx.Scheduler
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.schedulers.Schedulers
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class HotEntryPresenterTest {

    @Mock
    lateinit var getHotEntriesUsecase: GetHotEntriesUsecase

    @Mock
    lateinit var view: HotEntryContact.View

    lateinit var presenter: HotEntryPresenter

    @Before
    fun setUp() {
        RxAndroidPlugins.getInstance().registerSchedulersHook(object : RxAndroidSchedulersHook() {
            override fun getMainThreadScheduler(): Scheduler? {
                return Schedulers.immediate()
            }
        })

        presenter = HotEntryPresenter(getHotEntriesUsecase)
        presenter.onCreate(view, EntryTypeFilter.ALL)
    }

    @After
    fun tearDown() {
        presenter.onPause()
        RxAndroidPlugins.getInstance().reset()
    }

    @Test
    fun testOnResume_initialize_success() {

        val entryList: MutableList<EntryEntity> = mutableListOf()
        entryList.add(TestUtil.createTestEntryEntity(1))

        `when`(getHotEntriesUsecase.get(EntryTypeFilter.ALL)).thenReturn(Observable.just(entryList))

        presenter.onResume()

        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideEmpty()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showEntryList(entryList)
    }

    @Test
    fun testOnResume_initialize_empty() {

        `when`(getHotEntriesUsecase.get(EntryTypeFilter.ALL)).thenReturn(Observable.just(arrayListOf()))

        presenter.onResume()

        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showEmpty()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideEntryList()
    }

    @Test
    fun testOnResume_initialize_failure() {

        `when`(getHotEntriesUsecase.get(EntryTypeFilter.ALL)).thenReturn(TestUtil.createApiErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR))

        presenter.onResume()

        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showNetworkErrorMessage()
    }

    @Test
    fun testOnResume_initialize_restart() {

        val entryList: MutableList<EntryEntity> = mutableListOf()
        entryList.add(TestUtil.createTestEntryEntity(1))
        entryList.add(TestUtil.createTestEntryEntity(2))

        `when`(getHotEntriesUsecase.get(EntryTypeFilter.ALL)).thenReturn(Observable.just(entryList))

        presenter.onResume()

        Thread.sleep(1000)

        presenter.onPause()

        presenter.onResume()

        verify(getHotEntriesUsecase, timeout(TimeUnit.SECONDS.toMillis(1))).get(EntryTypeFilter.ALL)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1)).times(2)).showEntryList(entryList)
    }

    @Test
    fun testOnRefreshList() {

        val entryList: MutableList<EntryEntity> = mutableListOf()
        entryList.add(TestUtil.createTestEntryEntity(1))
        entryList.add(TestUtil.createTestEntryEntity(2))

        val nextEntryList: MutableList<EntryEntity> = mutableListOf()
        nextEntryList.add(TestUtil.createTestEntryEntity(3))

        val finallyDisplayList = mutableListOf<EntryEntity>()
        finallyDisplayList.addAll(entryList)
        finallyDisplayList.addAll(nextEntryList)

        `when`(getHotEntriesUsecase.get(EntryTypeFilter.ALL)).thenReturn(Observable.just(entryList))

        presenter.onResume()

        Thread.sleep(1000)

        `when`(getHotEntriesUsecase.get(EntryTypeFilter.ALL)).thenReturn(Observable.just(finallyDisplayList))

        presenter.onRefreshList()

        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showEntryList(finallyDisplayList)
    }

    @Test
    fun testOnOptionItemSelected() {

        val entryList: MutableList<EntryEntity> = mutableListOf()
        entryList.add(TestUtil.createTestEntryEntity(1))
        entryList.add(TestUtil.createTestEntryEntity(2))

        val nextEntryList: MutableList<EntryEntity> = mutableListOf()
        nextEntryList.add(TestUtil.createTestEntryEntity(3))
        nextEntryList.add(TestUtil.createTestEntryEntity(4))

        `when`(getHotEntriesUsecase.get(EntryTypeFilter.ALL)).thenReturn(Observable.just(entryList))

        presenter.onResume()

        Thread.sleep(1000)

        `when`(getHotEntriesUsecase.get(EntryTypeFilter.ANIMATION_AND_GAME)).thenReturn(Observable.just(nextEntryList))
        presenter.onOptionItemSelected(EntryTypeFilter.ANIMATION_AND_GAME)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showEntryList(nextEntryList)
    }

    @Test
    fun testOnClickEntry() {
        val entry = TestUtil.createTestEntryEntity(0)

        presenter.onClickEntry(entry)

        verify(view).navigateToBookmark(entry)
    }
}
