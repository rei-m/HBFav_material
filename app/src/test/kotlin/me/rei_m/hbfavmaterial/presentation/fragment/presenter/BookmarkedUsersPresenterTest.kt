package me.rei_m.hbfavmaterial.presentation.fragment.presenter

import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.enum.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkedUsersContact
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkedUsersPresenter
import me.rei_m.hbfavmaterial.testutil.TestUtil
import me.rei_m.hbfavmaterial.usecase.GetBookmarkedUsersUsecase
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
    lateinit var getBookmarkedUsersUsecase: GetBookmarkedUsersUsecase

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
        val presenter = BookmarkedUsersPresenter(getBookmarkedUsersUsecase)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.COMMENT)
        assertThat(presenter.bookmarkCommentFilter, `is`(BookmarkCommentFilter.COMMENT))
    }

    @Test
    fun testOnResume_initialize_success() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1))

        `when`(getBookmarkedUsersUsecase.get(bookmarkEntity)).thenReturn(Observable.just(bookmarkList))

        val presenter = BookmarkedUsersPresenter(getBookmarkedUsersUsecase)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.ALL)
        presenter.onResume()

        verify(getBookmarkedUsersUsecase, timeout(TimeUnit.SECONDS.toMillis(1))).get(bookmarkEntity)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideEmpty()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showUserList(bookmarkList)
    }

    @Test
    fun testOnResume_initialize_failure() {

        `when`(getBookmarkedUsersUsecase.get(bookmarkEntity))
                .thenReturn(Observable.error(TestUtil.createApiErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR)))

        val presenter = BookmarkedUsersPresenter(getBookmarkedUsersUsecase)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.ALL)
        presenter.onResume()

        verify(getBookmarkedUsersUsecase, timeout(TimeUnit.SECONDS.toMillis(1))).get(bookmarkEntity)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showNetworkErrorMessage()
    }

    @Test
    fun testOnResume_restart_all() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(0))
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1, "hoge"))

        val presenter = BookmarkedUsersPresenter(getBookmarkedUsersUsecase, bookmarkList)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.ALL)
        presenter.onResume()

        verify(getBookmarkedUsersUsecase, never()).get(bookmarkEntity)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showUserList(bookmarkList)
    }

    @Test
    fun testOnResume_restart_comment() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(0))
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1, "hoge"))

        val displayedBookmarkList = arrayListOf(bookmarkList[1])

        val presenter = BookmarkedUsersPresenter(getBookmarkedUsersUsecase, bookmarkList)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.COMMENT)
        presenter.onResume()

        verify(getBookmarkedUsersUsecase, never()).get(bookmarkEntity)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showUserList(displayedBookmarkList)
    }

    @Test
    fun testOnRefreshList() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(0))

        `when`(getBookmarkedUsersUsecase.get(bookmarkEntity)).thenReturn(Observable.just(bookmarkList))

        val presenter = BookmarkedUsersPresenter(getBookmarkedUsersUsecase, bookmarkList)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.ALL)
        presenter.onResume()
        presenter.onRefreshList()

        verify(getBookmarkedUsersUsecase, timeout(TimeUnit.SECONDS.toMillis(1))).get(bookmarkEntity)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1)).times(2)).showUserList(bookmarkList)
    }

    @Test
    fun testOnOptionItemSelected_all() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(TestUtil.createTestBookmarkEntity(0))
        bookmarkList.add(TestUtil.createTestBookmarkEntity(1, "hoge"))

        val presenter = BookmarkedUsersPresenter(getBookmarkedUsersUsecase, bookmarkList)
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

        val presenter = BookmarkedUsersPresenter(getBookmarkedUsersUsecase, bookmarkList)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.ALL)
        presenter.onOptionItemSelected(BookmarkCommentFilter.COMMENT)

        verify(view, timeout(TimeUnit.SECONDS.toMillis(1)).times(1)).showUserList(displayedBookmarkList)
    }

    @Test
    fun testOnClickUser() {

        val bookmark = TestUtil.createTestBookmarkEntity(0)

        val presenter = BookmarkedUsersPresenter(getBookmarkedUsersUsecase)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.ALL)

        presenter.onClickUser(bookmark)

        verify(view).navigateToOthersBookmark(bookmark)
    }
}