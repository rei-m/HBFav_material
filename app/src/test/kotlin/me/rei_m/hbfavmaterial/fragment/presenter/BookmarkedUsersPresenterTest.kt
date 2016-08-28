package me.rei_m.hbfavmaterial.fragment.presenter

import me.rei_m.hbfavmaterial.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.enum.BookmarkCommentFilter
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
class BookmarkedUsersPresenterTest {

    @Mock
    lateinit var bookmarkService: BookmarkService

    @Mock
    lateinit var view: BookmarkedUsersContact.View

    private val bookmarkEntity = TestUtil.createTestBookmarkEntity(1)

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
    fun testOnCreate() {
        val presenter = BookmarkedUsersPresenter(bookmarkService)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.COMMENT)
        assertThat(presenter.bookmarkCommentFilter, `is`(BookmarkCommentFilter.COMMENT))
    }

    @Test
    fun testOnResume_initialize_success() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1))

        `when`(bookmarkService.findByArticleUrl(bookmarkEntity.articleEntity.url)).thenReturn(Observable.just(bookmarkList))

        val presenter = BookmarkedUsersPresenter(bookmarkService)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.ALL)
        presenter.onResume()

        verify(bookmarkService, timeout(TimeUnit.SECONDS.toMillis(1))).findByArticleUrl(bookmarkEntity.articleEntity.url)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideEmpty()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showUserList(bookmarkList)
    }

    @Test
    fun testOnResume_initialize_failure() {

        `when`(bookmarkService.findByArticleUrl(bookmarkEntity.articleEntity.url))
                .thenReturn(Observable.error(TestUtil.createApiErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR)))

        val presenter = BookmarkedUsersPresenter(bookmarkService)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.ALL)
        presenter.onResume()

        verify(bookmarkService, timeout(TimeUnit.SECONDS.toMillis(1))).findByArticleUrl(bookmarkEntity.articleEntity.url)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showNetworkErrorMessage()
    }

    @Test
    fun testOnResume_restart_all() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(0))
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1, "hoge"))

        val presenter = BookmarkedUsersPresenter(bookmarkService, bookmarkList)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.ALL)
        presenter.onResume()

        verify(bookmarkService, never()).findByArticleUrl(bookmarkEntity.articleEntity.url)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showUserList(bookmarkList)
    }

    @Test
    fun testOnResume_restart_comment() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(0))
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1, "hoge"))

        val displayedBookmarkList = arrayListOf(bookmarkList[1])

        val presenter = BookmarkedUsersPresenter(bookmarkService, bookmarkList)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.COMMENT)
        presenter.onResume()

        verify(bookmarkService, never()).findByArticleUrl(bookmarkEntity.articleEntity.url)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showUserList(displayedBookmarkList)
    }

    @Test
    fun testOnRefreshList() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(0))

        `when`(bookmarkService.findByArticleUrl(bookmarkEntity.articleEntity.url)).thenReturn(Observable.just(bookmarkList))

        val presenter = BookmarkedUsersPresenter(bookmarkService, bookmarkList)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.ALL)
        presenter.onResume()
        presenter.onRefreshList()

        verify(bookmarkService, timeout(TimeUnit.SECONDS.toMillis(1))).findByArticleUrl(bookmarkEntity.articleEntity.url)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1)).times(2)).showUserList(bookmarkList)
    }

    @Test
    fun testOnOptionItemSelected_all() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(0))
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1, "hoge"))

        val presenter = BookmarkedUsersPresenter(bookmarkService, bookmarkList)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.COMMENT)
        presenter.onOptionItemSelected(BookmarkCommentFilter.ALL)

        verify(view, timeout(TimeUnit.SECONDS.toMillis(1)).times(1)).showUserList(bookmarkList)
    }

    @Test
    fun testOnOptionItemSelected_comment() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(0))
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1, "hoge"))

        val displayedBookmarkList = arrayListOf(bookmarkList[1])

        val presenter = BookmarkedUsersPresenter(bookmarkService, bookmarkList)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.ALL)
        presenter.onOptionItemSelected(BookmarkCommentFilter.COMMENT)

        verify(view, timeout(TimeUnit.SECONDS.toMillis(1)).times(1)).showUserList(displayedBookmarkList)
    }

    @Test
    fun testOnClickUser() {

        val bookmark = TestUtil.createTestBookmarkEntity(0)

        val presenter = BookmarkedUsersPresenter(bookmarkService)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.ALL)

        presenter.onClickUser(bookmark)

        verify(view).navigateToOthersBookmark(bookmark)
    }
}