//package me.rei_m.hbfavmaterial.presentation.fragment
//
//import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
//import me.rei_m.hbfavmaterial.testutil.TestUtil
//import me.rei_m.hbfavmaterial.usecase.GetFavoriteBookmarksUsecase
//import org.junit.After
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.Mock
//import org.mockito.Mockito.*
//import org.mockito.runners.MockitoJUnitRunner
//import rx.Observable
//import rx.Scheduler
//import rx.android.plugins.RxAndroidPlugins
//import rx.android.plugins.RxAndroidSchedulersHook
//import rx.schedulers.Schedulers
//import java.net.HttpURLConnection
//import java.util.concurrent.TimeUnit
//
//@RunWith(MockitoJUnitRunner::class)
//class BookmarkFavoritePresenterTest {
//
//    @Mock
//    lateinit var getFavoriteBookmarksUsecase: GetFavoriteBookmarksUsecase
//
//    @Mock
//    lateinit var view: BookmarkFavoriteContact.View
//
//    lateinit var presenter: BookmarkFavoritePresenter
//
//    @Before
//    fun setUp() {
//        RxAndroidPlugins.getInstance().registerSchedulersHook(object : RxAndroidSchedulersHook() {
//            override fun getMainThreadScheduler(): Scheduler? {
//                return Schedulers.immediate()
//            }
//        })
//
//        presenter = BookmarkFavoritePresenter(getFavoriteBookmarksUsecase)
//        presenter.onCreate(view)
//    }
//
//    @After
//    fun tearDown() {
//        presenter.onPause()
//        RxAndroidPlugins.getInstance().reset()
//    }
//
//    @Test
//    fun testOnResume_initialize_success() {
//
//        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
//        bookmarkList.add(TestUtil.createTestBookmarkEntity(1))
//
//        `when`(getFavoriteBookmarksUsecase.get()).thenReturn(Observable.just(bookmarkList))
//
//        presenter.onResume()
//
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideEmpty()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).startAutoLoading()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showBookmarkList(bookmarkList)
//    }
//
//    @Test
//    fun testOnResume_initialize_empty() {
//
//        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
//
//        `when`(getFavoriteBookmarksUsecase.get()).thenReturn(Observable.just(bookmarkList))
//
//        presenter.onResume()
//
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideBookmarkList()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showEmpty()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).stopAutoLoading()
//    }
//
//    @Test
//    fun testOnResume_initialize_failure() {
//
//        `when`(getFavoriteBookmarksUsecase.get()).thenReturn(TestUtil.createApiErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR))
//
//        presenter.onResume()
//
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showNetworkErrorMessage()
//    }
//
//    @Test
//    fun testOnResume_restart() {
//
//        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
//        bookmarkList.add(TestUtil.createTestBookmarkEntity(1))
//        bookmarkList.add(TestUtil.createTestBookmarkEntity(2))
//
//        `when`(getFavoriteBookmarksUsecase.get()).thenReturn(Observable.just(bookmarkList))
//
//        presenter.onResume()
//
//        Thread.sleep(1000)
//
//        presenter.onPause()
//
//        presenter.onResume()
//
//        verify(getFavoriteBookmarksUsecase, timeout(TimeUnit.SECONDS.toMillis(1))).get()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1)).times(2)).showBookmarkList(bookmarkList)
//    }
//
//    @Test
//    fun testOnRefreshList() {
//
//        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
//        bookmarkList.add(TestUtil.createTestBookmarkEntity(1))
//        bookmarkList.add(TestUtil.createTestBookmarkEntity(2))
//
//        val nextBookmarkList: MutableList<BookmarkEntity> = mutableListOf()
//        nextBookmarkList.add(TestUtil.createTestBookmarkEntity(3))
//
//        val finallyDisplayList = mutableListOf<BookmarkEntity>()
//        finallyDisplayList.addAll(bookmarkList)
//        finallyDisplayList.addAll(nextBookmarkList)
//
//        `when`(getFavoriteBookmarksUsecase.get()).thenReturn(Observable.just(bookmarkList))
//
//        presenter.onResume()
//
//        Thread.sleep(1000)
//
//        `when`(getFavoriteBookmarksUsecase.get()).thenReturn(Observable.just(finallyDisplayList))
//
//        presenter.onRefreshList()
//
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showBookmarkList(finallyDisplayList)
//    }
//
//    @Test
//    fun testOnScrollEnd() {
//
//        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
//        bookmarkList.add(TestUtil.createTestBookmarkEntity(1))
//        bookmarkList.add(TestUtil.createTestBookmarkEntity(2))
//
//        val nextBookmarkList: MutableList<BookmarkEntity> = mutableListOf()
//        nextBookmarkList.add(TestUtil.createTestBookmarkEntity(3))
//
//        val finallyDisplayList = mutableListOf<BookmarkEntity>()
//        finallyDisplayList.addAll(bookmarkList)
//        finallyDisplayList.addAll(nextBookmarkList)
//
//        `when`(getFavoriteBookmarksUsecase.get()).thenReturn(Observable.just(bookmarkList))
//
//        presenter.onResume()
//
//        Thread.sleep(1000)
//
//        `when`(getFavoriteBookmarksUsecase.get(2)).thenReturn(Observable.just(nextBookmarkList))
//
//        presenter.onScrollEnd(2)
//
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showBookmarkList(finallyDisplayList)
//    }
//
//    @Test
//    fun testOnClickBookmark() {
//        val bookmark = TestUtil.createTestBookmarkEntity(0)
//
//        val presenter = BookmarkFavoritePresenter(getFavoriteBookmarksUsecase)
//        presenter.onCreate(view)
//
//        presenter.onClickBookmark(bookmark)
//
//        verify(view).navigateToBookmark(bookmark)
//    }
//}
