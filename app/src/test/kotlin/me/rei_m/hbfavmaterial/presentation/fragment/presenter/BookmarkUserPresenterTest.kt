package me.rei_m.hbfavmaterial.presentation.fragment.presenter

import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.enum.ReadAfterFilter
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkUserContact
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkUserPresenter
import me.rei_m.hbfavmaterial.testutil.TestUtil
import me.rei_m.hbfavmaterial.usecase.GetUserBookmarksUsecase
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
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
class BookmarkUserPresenterTest {

    @Mock
    lateinit var getUserBookmarksUsecase: GetUserBookmarksUsecase

    @Mock
    lateinit var view: BookmarkUserContact.View

    @Before
    fun setUp() {
        RxAndroidPlugins.getInstance().registerSchedulersHook(object : RxAndroidSchedulersHook() {
            override fun getMainThreadScheduler(): Scheduler? {
                return Schedulers.immediate()
            }
        })
    }

    @After
    fun tearDown() {
        RxAndroidPlugins.getInstance().reset()
    }

    @Test
    fun testOnCreate_owner() {

        `when`(getUserBookmarksUsecase.get(ReadAfterFilter.ALL)).thenReturn(Observable.just(arrayListOf()))

        val presenter = BookmarkUserPresenter(getUserBookmarksUsecase)
        presenter.onCreate(view, true, "", ReadAfterFilter.ALL)
        presenter.onResume()

        assertThat(presenter.readAfterFilter, `is`(ReadAfterFilter.ALL))
        verify(getUserBookmarksUsecase, timeout(TimeUnit.SECONDS.toMillis(1))).get(ReadAfterFilter.ALL)
    }

    @Test
    fun testOnCreate_other() {

        `when`(getUserBookmarksUsecase.get("hoge", ReadAfterFilter.AFTER_READ)).thenReturn(Observable.just(arrayListOf()))

        val presenter = BookmarkUserPresenter(getUserBookmarksUsecase)
        presenter.onCreate(view, false, "hoge", ReadAfterFilter.AFTER_READ)
        presenter.onResume()

        assertThat(presenter.readAfterFilter, `is`(ReadAfterFilter.AFTER_READ))
        verify(getUserBookmarksUsecase, timeout(TimeUnit.SECONDS.toMillis(1))).get("hoge", ReadAfterFilter.AFTER_READ)
    }

    @Test
    fun testOnResume_initialize_success_all() {
        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1))

        `when`(getUserBookmarksUsecase.get(ReadAfterFilter.ALL)).thenReturn(Observable.just(bookmarkList))

        val presenter = BookmarkUserPresenter(getUserBookmarksUsecase)
        presenter.onCreate(view, true, "", ReadAfterFilter.ALL)
        presenter.onResume()

        verify(getUserBookmarksUsecase, timeout(TimeUnit.SECONDS.toMillis(1))).get(ReadAfterFilter.ALL)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideEmpty()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).startAutoLoading()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showBookmarkList(bookmarkList)
    }

    @Test
    fun testOnResume_initialize_success_readAfter() {
        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1))

        `when`(getUserBookmarksUsecase.get(ReadAfterFilter.AFTER_READ)).thenReturn(Observable.just(bookmarkList))

        val presenter = BookmarkUserPresenter(getUserBookmarksUsecase)
        presenter.onCreate(view, true, "", ReadAfterFilter.AFTER_READ)
        presenter.onResume()

        verify(getUserBookmarksUsecase, timeout(TimeUnit.SECONDS.toMillis(1))).get(ReadAfterFilter.AFTER_READ)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideEmpty()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).startAutoLoading()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showBookmarkList(bookmarkList)
    }

    @Test
    fun testOnResume_initialize_empty() {

        `when`(getUserBookmarksUsecase.get(ReadAfterFilter.ALL)).thenReturn(Observable.just(arrayListOf()))

        val presenter = BookmarkUserPresenter(getUserBookmarksUsecase)
        presenter.onCreate(view, true, "", ReadAfterFilter.ALL)
        presenter.onResume()

        verify(getUserBookmarksUsecase, timeout(TimeUnit.SECONDS.toMillis(1))).get(ReadAfterFilter.ALL)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideBookmarkList()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showEmpty()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).stopAutoLoading()
    }

    @Test
    fun testOnResume_initialize_failure() {

        `when`(getUserBookmarksUsecase.get(ReadAfterFilter.ALL)).thenReturn(TestUtil.createApiErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR))

        val presenter = BookmarkUserPresenter(getUserBookmarksUsecase)
        presenter.onCreate(view, true, "", ReadAfterFilter.ALL)
        presenter.onResume()

        verify(getUserBookmarksUsecase, timeout(TimeUnit.SECONDS.toMillis(1))).get(ReadAfterFilter.ALL)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showNetworkErrorMessage()
    }

    @Test
    fun testOnResume_restart() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1))
        bookmarkList.add(TestUtil.createTestBookmarkEntity(2))

        val presenter = BookmarkUserPresenter(getUserBookmarksUsecase, bookmarkList)
        presenter.onCreate(view, true, "", ReadAfterFilter.ALL)
        presenter.onResume()

        verify(getUserBookmarksUsecase, never()).get(ReadAfterFilter.ALL)
        verify(view, never()).showProgress()
        verify(view, never()).hideProgress()
        verify(view).showBookmarkList(bookmarkList)
    }

    @Test
    fun testOnRefreshList() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1))
        bookmarkList.add(TestUtil.createTestBookmarkEntity(2))

        val nextBookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        nextBookmarkList.add(TestUtil.createTestBookmarkEntity(3))

        val finallyDisplayList = mutableListOf<BookmarkEntity>()
        finallyDisplayList.addAll(bookmarkList)
        finallyDisplayList.addAll(nextBookmarkList)

        `when`(getUserBookmarksUsecase.get(ReadAfterFilter.ALL)).thenReturn(Observable.just(finallyDisplayList))

        val presenter = BookmarkUserPresenter(getUserBookmarksUsecase, bookmarkList)
        presenter.onCreate(view, true, "", ReadAfterFilter.ALL)
        presenter.onResume()
        presenter.onRefreshList()

        verify(getUserBookmarksUsecase, timeout(TimeUnit.SECONDS.toMillis(1))).get(ReadAfterFilter.ALL)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1)).times(2)).showBookmarkList(finallyDisplayList)
    }

    @Test
    fun testOnScrollEnd() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1))
        bookmarkList.add(TestUtil.createTestBookmarkEntity(2))

        val nextBookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        nextBookmarkList.add(TestUtil.createTestBookmarkEntity(3))

        val finallyDisplayList = mutableListOf<BookmarkEntity>()
        finallyDisplayList.addAll(bookmarkList)
        finallyDisplayList.addAll(nextBookmarkList)

        `when`(getUserBookmarksUsecase.get(ReadAfterFilter.ALL, 2)).thenReturn(Observable.just(nextBookmarkList))

        val presenter = BookmarkUserPresenter(getUserBookmarksUsecase, bookmarkList)
        presenter.onCreate(view, true, "", ReadAfterFilter.ALL)
        presenter.onResume()
        presenter.onScrollEnd(2)

        verify(getUserBookmarksUsecase, timeout(TimeUnit.SECONDS.toMillis(1))).get(ReadAfterFilter.ALL, 2)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1)).times(2)).showBookmarkList(finallyDisplayList)
    }

    @Test
    fun testOnOptionItemSelected() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1))
        bookmarkList.add(TestUtil.createTestBookmarkEntity(2))

        val nextBookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        nextBookmarkList.add(TestUtil.createTestBookmarkEntity(3))

        `when`(getUserBookmarksUsecase.get(ReadAfterFilter.AFTER_READ)).thenReturn(Observable.just(nextBookmarkList))

        val presenter = BookmarkUserPresenter(getUserBookmarksUsecase, bookmarkList)
        presenter.onCreate(view, true, "", ReadAfterFilter.ALL)
        presenter.onResume()
        presenter.onOptionItemSelected(ReadAfterFilter.AFTER_READ)
        presenter.onOptionItemSelected(ReadAfterFilter.AFTER_READ)

        verify(getUserBookmarksUsecase, timeout(TimeUnit.SECONDS.toMillis(1))).get(ReadAfterFilter.AFTER_READ)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideEmpty()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).startAutoLoading()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1)).times(2)).showBookmarkList(nextBookmarkList)
    }

    @Test
    fun testOnClickBookmark() {

        val bookmark = TestUtil.createTestBookmarkEntity(0)

        val presenter = BookmarkUserPresenter(getUserBookmarksUsecase)
        presenter.onCreate(view, true, "", ReadAfterFilter.ALL)

        presenter.onClickBookmark(bookmark)

        verify(view).navigateToBookmark(bookmark)
    }
}