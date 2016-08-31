package me.rei_m.hbfavmaterial.fragment.presenter

import me.rei_m.hbfavmaterial.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.entity.UserEntity
import me.rei_m.hbfavmaterial.enum.ReadAfterFilter
import me.rei_m.hbfavmaterial.repository.UserRepository
import me.rei_m.hbfavmaterial.service.BookmarkService
import me.rei_m.hbfavmaterial.testutil.TestUtil
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
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var bookmarkService: BookmarkService

    @Mock
    lateinit var view: BookmarkUserContact.View

    @Before
    fun setUp() {

        `when`(userRepository.resolve()).thenReturn(UserEntity("test"))

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

        val presenter = BookmarkUserPresenter(userRepository, bookmarkService)
        presenter.onCreate(view, true, "", ReadAfterFilter.ALL)

        assertThat(presenter.readAfterFilter, `is`(ReadAfterFilter.ALL))
        verify(userRepository).resolve()
    }

    @Test
    fun testOnCreate_other() {
        val presenter = BookmarkUserPresenter(userRepository, bookmarkService)
        presenter.onCreate(view, false, "hoge", ReadAfterFilter.AFTER_READ)

        assertThat(presenter.readAfterFilter, `is`(ReadAfterFilter.AFTER_READ))
        verify(userRepository, never()).resolve()
    }

    @Test
    fun testOnResume_initialize_success_all() {
        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1))

        `when`(bookmarkService.findByUserId(userRepository.resolve().id, ReadAfterFilter.ALL))
                .thenReturn(Observable.just(bookmarkList))

        val presenter = BookmarkUserPresenter(userRepository, bookmarkService)
        presenter.onCreate(view, true, "", ReadAfterFilter.ALL)
        presenter.onResume()

        verify(bookmarkService, timeout(TimeUnit.SECONDS.toMillis(1))).findByUserId(userRepository.resolve().id, ReadAfterFilter.ALL)
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

        `when`(bookmarkService.findByUserId(userRepository.resolve().id, ReadAfterFilter.AFTER_READ))
                .thenReturn(Observable.just(bookmarkList))

        val presenter = BookmarkUserPresenter(userRepository, bookmarkService)
        presenter.onCreate(view, true, "", ReadAfterFilter.AFTER_READ)
        presenter.onResume()

        verify(bookmarkService, timeout(TimeUnit.SECONDS.toMillis(1))).findByUserId(userRepository.resolve().id, ReadAfterFilter.AFTER_READ)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideEmpty()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).startAutoLoading()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showBookmarkList(bookmarkList)
    }

    @Test
    fun testOnResume_initialize_empty() {

        `when`(bookmarkService.findByUserId(userRepository.resolve().id, ReadAfterFilter.ALL))
                .thenReturn(Observable.just(arrayListOf()))

        val presenter = BookmarkUserPresenter(userRepository, bookmarkService)
        presenter.onCreate(view, true, "", ReadAfterFilter.ALL)
        presenter.onResume()

        verify(bookmarkService, timeout(TimeUnit.SECONDS.toMillis(1))).findByUserId(userRepository.resolve().id, ReadAfterFilter.ALL)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideBookmarkList()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showEmpty()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).stopAutoLoading()
    }

    @Test
    fun testOnResume_initialize_failure() {

        `when`(bookmarkService.findByUserId(userRepository.resolve().id, ReadAfterFilter.ALL))
                .thenReturn(Observable.error(TestUtil.createApiErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR)))

        val presenter = BookmarkUserPresenter(userRepository, bookmarkService)
        presenter.onCreate(view, true, "", ReadAfterFilter.ALL)
        presenter.onResume()

        verify(bookmarkService, timeout(TimeUnit.SECONDS.toMillis(1))).findByUserId(userRepository.resolve().id, ReadAfterFilter.ALL)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showNetworkErrorMessage()
    }

    @Test
    fun testOnResume_restart() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1))
        bookmarkList.add(TestUtil.createTestBookmarkEntity(2))

        val presenter = BookmarkUserPresenter(userRepository, bookmarkService, bookmarkList)
        presenter.onCreate(view, true, "", ReadAfterFilter.ALL)
        presenter.onResume()

        verify(bookmarkService, never()).findByUserId(userRepository.resolve().id, ReadAfterFilter.ALL)
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

        `when`(bookmarkService.findByUserId(userRepository.resolve().id, ReadAfterFilter.ALL))
                .thenReturn(Observable.just(finallyDisplayList))

        val presenter = BookmarkUserPresenter(userRepository, bookmarkService, bookmarkList)
        presenter.onCreate(view, true, "", ReadAfterFilter.ALL)
        presenter.onResume()
        presenter.onRefreshList()

        verify(bookmarkService, timeout(TimeUnit.SECONDS.toMillis(1))).findByUserId(userRepository.resolve().id, ReadAfterFilter.ALL)
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

        `when`(bookmarkService.findByUserId(userRepository.resolve().id, ReadAfterFilter.ALL, 2)).thenReturn(Observable.just(nextBookmarkList))

        val presenter = BookmarkUserPresenter(userRepository, bookmarkService, bookmarkList)
        presenter.onCreate(view, true, "", ReadAfterFilter.ALL)
        presenter.onResume()
        presenter.onScrollEnd(2)

        verify(bookmarkService, timeout(TimeUnit.SECONDS.toMillis(1))).findByUserId(userRepository.resolve().id, ReadAfterFilter.ALL, 2)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1)).times(2)).showBookmarkList(finallyDisplayList)
    }

    @Test
    fun testOnOptionItemSelected() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1))
        bookmarkList.add(TestUtil.createTestBookmarkEntity(2))

        val nextBookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        nextBookmarkList.add(TestUtil.createTestBookmarkEntity(3))

        `when`(bookmarkService.findByUserId(userRepository.resolve().id, ReadAfterFilter.AFTER_READ))
                .thenReturn(Observable.just(nextBookmarkList))

        val presenter = BookmarkUserPresenter(userRepository, bookmarkService, bookmarkList)
        presenter.onCreate(view, true, "", ReadAfterFilter.ALL)
        presenter.onResume()
        presenter.onOptionItemSelected(ReadAfterFilter.AFTER_READ)
        presenter.onOptionItemSelected(ReadAfterFilter.AFTER_READ)

        verify(bookmarkService, timeout(TimeUnit.SECONDS.toMillis(1))).findByUserId(userRepository.resolve().id, ReadAfterFilter.AFTER_READ)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideEmpty()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).startAutoLoading()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1)).times(2)).showBookmarkList(nextBookmarkList)
    }

    @Test
    fun testOnClickBookmark() {

        val bookmark = TestUtil.createTestBookmarkEntity(0)

        val presenter = BookmarkUserPresenter(userRepository, bookmarkService)
        presenter.onCreate(view, true, "", ReadAfterFilter.ALL)

        presenter.onClickBookmark(bookmark)

        verify(view).navigateToBookmark(bookmark)
    }
}