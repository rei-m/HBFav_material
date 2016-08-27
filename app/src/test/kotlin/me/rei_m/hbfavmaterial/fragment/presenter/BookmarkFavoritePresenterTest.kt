package me.rei_m.hbfavmaterial.fragment.presenter

import me.rei_m.hbfavmaterial.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.entity.UserEntity
import me.rei_m.hbfavmaterial.repository.UserRepository
import me.rei_m.hbfavmaterial.service.BookmarkService
import me.rei_m.hbfavmaterial.testutil.TestUtil
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
class BookmarkFavoritePresenterTest {

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var bookmarkService: BookmarkService

    @Mock
    lateinit var view: BookmarkFavoriteContact.View

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
    fun testOnResume_initialize_success() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1))

        `when`(bookmarkService.findByUserIdForFavorite(userRepository.resolve().id))
                .thenReturn(Observable.just(bookmarkList))

        doAnswer { Unit }.`when`(view).showBookmarkList(bookmarkList)

        val presenter = BookmarkFavoritePresenter(userRepository, bookmarkService)
        presenter.onCreate(view)
        presenter.onResume()

        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideEmpty()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).startAutoLoading()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showBookmarkList(bookmarkList)
    }

    @Test
    fun testOnResume_initialize_empty() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()

        `when`(bookmarkService.findByUserIdForFavorite(userRepository.resolve().id))
                .thenReturn(Observable.just(bookmarkList))

        val presenter = BookmarkFavoritePresenter(userRepository, bookmarkService)
        presenter.onCreate(view)
        presenter.onResume()

        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideBookmarkList()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showEmpty()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).stopAutoLoading()
    }

    @Test
    fun testOnResume_initialize_failure() {

        `when`(bookmarkService.findByUserIdForFavorite(userRepository.resolve().id))
                .thenReturn(Observable.error(TestUtil.createApiErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR)))

        val presenter = BookmarkFavoritePresenter(userRepository, bookmarkService)
        presenter.onCreate(view)
        presenter.onResume()

        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showNetworkErrorMessage()
    }

    @Test
    fun testOnRefreshList() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1))

        `when`(bookmarkService.findByUserIdForFavorite(userRepository.resolve().id)).thenReturn(Observable.just(bookmarkList))

        doAnswer { Unit }.`when`(view).showBookmarkList(bookmarkList)

        val presenter = BookmarkFavoritePresenter(userRepository, bookmarkService)
        presenter.onCreate(view)
        presenter.onResume()

        Thread.sleep(100)

        presenter.onRefreshList()

        verify(bookmarkService, timeout(TimeUnit.SECONDS.toMillis(1)).times(2)).findByUserIdForFavorite(userRepository.resolve().id)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showBookmarkList(bookmarkList)
    }

    @Test
    fun testOnScrollEnd() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1))

        val nextBookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        nextBookmarkList.add(TestUtil.createTestBookmarkEntity(2))

        val finallyDisplayList = mutableListOf<BookmarkEntity>()
        finallyDisplayList.addAll(bookmarkList)
        finallyDisplayList.addAll(nextBookmarkList)

        `when`(bookmarkService.findByUserIdForFavorite(userRepository.resolve().id)).thenReturn(Observable.just(bookmarkList))
        `when`(bookmarkService.findByUserIdForFavorite(userRepository.resolve().id, 2)).thenReturn(Observable.just(nextBookmarkList))

        doAnswer { Unit }.`when`(view).showBookmarkList(finallyDisplayList)

        val presenter = BookmarkFavoritePresenter(userRepository, bookmarkService)
        presenter.onCreate(view)
        presenter.onResume()

        Thread.sleep(100)

        presenter.onScrollEnd(2)

        verify(bookmarkService, timeout(TimeUnit.SECONDS.toMillis(1)).times(1)).findByUserIdForFavorite(userRepository.resolve().id)
        verify(bookmarkService, timeout(TimeUnit.SECONDS.toMillis(1)).times(1)).findByUserIdForFavorite(userRepository.resolve().id, 2)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showBookmarkList(finallyDisplayList)
    }

    @Test
    fun testOnClickBookmark() {
        val bookmark = TestUtil.createTestBookmarkEntity(0)

        doAnswer { Unit }.`when`(view).navigateToBookmark(bookmark)

        val presenter = BookmarkFavoritePresenter(userRepository, bookmarkService)
        presenter.onCreate(view)

        presenter.onClickBookmark(bookmark)

        verify(view).navigateToBookmark(bookmark)
    }
}
