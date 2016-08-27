package me.rei_m.hbfavmaterial.fragment.presenter

import me.rei_m.hbfavmaterial.entity.ArticleEntity
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
import java.util.*
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class BookmarkedUsersPresenterTest {

    @Mock
    lateinit var bookmarkService: BookmarkService

    @Mock
    lateinit var view: BookmarkedUsersContact.View

    private val bookmarkEntity = createTestBookmarkEntity(0, "")

    private fun createTestBookmarkEntity(no: Int, description: String): BookmarkEntity {
        return BookmarkEntity(
                articleEntity = ArticleEntity(
                        title = "ArticleEntity_title_$no",
                        url = "ArticleEntity_url_$no",
                        bookmarkCount = no,
                        iconUrl = "ArticleEntity_iconUrl_$no",
                        body = "ArticleEntity_body_$no",
                        bodyImageUrl = "ArticleEntity_bodyImageUrl_$no"
                ),
                description = description,
                creator = "BookmarkEntity_creator_$no",
                date = Date(),
                bookmarkIconUrl = "BookmarkEntity_bookmarkIconUrl_$no")
    }

    @Before
    fun setUp() {

        doAnswer { Unit }.`when`(view).hideEmpty()
        doAnswer { Unit }.`when`(view).hideProgress()
        doAnswer { Unit }.`when`(view).hideUserList()
        doAnswer { Unit }.`when`(view).showEmpty()
        doAnswer { Unit }.`when`(view).showNetworkErrorMessage()
        doAnswer { Unit }.`when`(view).showProgress()

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
        bookmarkList.add(createTestBookmarkEntity(0, ""))

        `when`(bookmarkService.findByArticleUrl(bookmarkEntity.articleEntity.url)).thenReturn(Observable.just(bookmarkList))

        doAnswer { Unit }.`when`(view).showUserList(bookmarkList)

        val presenter = BookmarkedUsersPresenter(bookmarkService)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.ALL)
        presenter.onResume()

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

        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showNetworkErrorMessage()
    }

    @Test
    fun testOnResume_restart_all() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(createTestBookmarkEntity(0, ""))

        `when`(bookmarkService.findByArticleUrl(bookmarkEntity.articleEntity.url)).thenReturn(Observable.just(bookmarkList))

        doAnswer { Unit }.`when`(view).showUserList(bookmarkList)

        val presenter = BookmarkedUsersPresenter(bookmarkService)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.ALL)
        presenter.onResume()

        Thread.sleep(100)

        presenter.onPause()

        presenter.onResume()

        verify(bookmarkService).findByArticleUrl(bookmarkEntity.articleEntity.url)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1)).times(2)).showUserList(bookmarkList)
    }

    @Test
    fun testOnResume_restart_comment() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(createTestBookmarkEntity(0, ""))
        bookmarkList.add(createTestBookmarkEntity(1, "hoge"))

        `when`(bookmarkService.findByArticleUrl(bookmarkEntity.articleEntity.url)).thenReturn(Observable.just(bookmarkList))

        val displayedBookmarkList = arrayListOf(bookmarkList[1])

        doAnswer { Unit }.`when`(view).showUserList(displayedBookmarkList)

        val presenter = BookmarkedUsersPresenter(bookmarkService)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.COMMENT)
        presenter.onResume()

        Thread.sleep(100)

        presenter.onPause()

        presenter.onResume()

        verify(bookmarkService).findByArticleUrl(bookmarkEntity.articleEntity.url)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1)).times(2)).showUserList(displayedBookmarkList)
    }

    @Test
    fun testOnRefreshList() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(createTestBookmarkEntity(0, ""))

        `when`(bookmarkService.findByArticleUrl(bookmarkEntity.articleEntity.url)).thenReturn(Observable.just(bookmarkList))

        doAnswer { Unit }.`when`(view).showUserList(bookmarkList)

        val presenter = BookmarkedUsersPresenter(bookmarkService)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.ALL)
        presenter.onResume()

        Thread.sleep(100)

        presenter.onRefreshList()

        verify(bookmarkService, timeout(TimeUnit.SECONDS.toMillis(1)).times(2)).findByArticleUrl(bookmarkEntity.articleEntity.url)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1)).times(2)).showUserList(bookmarkList)
    }

    @Test
    fun testOnOptionItemSelected_all() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(createTestBookmarkEntity(0, ""))
        bookmarkList.add(createTestBookmarkEntity(1, "hoge"))

        `when`(bookmarkService.findByArticleUrl(bookmarkEntity.articleEntity.url)).thenReturn(Observable.just(bookmarkList))

        val displayedBookmarkList = arrayListOf(bookmarkList[1])

        doAnswer { Unit }.`when`(view).showUserList(bookmarkList)
        doAnswer { Unit }.`when`(view).showUserList(displayedBookmarkList)

        val presenter = BookmarkedUsersPresenter(bookmarkService)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.COMMENT)
        presenter.onResume()

        Thread.sleep(100)

        presenter.onOptionItemSelected(BookmarkCommentFilter.ALL)

        verify(bookmarkService).findByArticleUrl(bookmarkEntity.articleEntity.url)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1)).times(1)).showUserList(displayedBookmarkList)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1)).times(1)).showUserList(bookmarkList)
    }

    @Test
    fun testOnOptionItemSelected_comment() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(createTestBookmarkEntity(0, ""))
        bookmarkList.add(createTestBookmarkEntity(1, "hoge"))

        `when`(bookmarkService.findByArticleUrl(bookmarkEntity.articleEntity.url)).thenReturn(Observable.just(bookmarkList))

        val displayedBookmarkList = arrayListOf(bookmarkList[1])

        doAnswer { Unit }.`when`(view).showUserList(bookmarkList)
        doAnswer { Unit }.`when`(view).showUserList(displayedBookmarkList)

        val presenter = BookmarkedUsersPresenter(bookmarkService)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.ALL)
        presenter.onResume()

        Thread.sleep(100)

        presenter.onOptionItemSelected(BookmarkCommentFilter.COMMENT)

        verify(bookmarkService).findByArticleUrl(bookmarkEntity.articleEntity.url)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1)).times(1)).showUserList(displayedBookmarkList)
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1)).times(1)).showUserList(bookmarkList)
    }

    @Test
    fun testOnClickUser() {

        val bookmark = createTestBookmarkEntity(0, "")

        doAnswer { Unit }.`when`(view).navigateToOthersBookmark(bookmark)

        val presenter = BookmarkedUsersPresenter(bookmarkService)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.ALL)

        presenter.onClickUser(bookmark)

        verify(view).navigateToOthersBookmark(bookmark)
    }
}