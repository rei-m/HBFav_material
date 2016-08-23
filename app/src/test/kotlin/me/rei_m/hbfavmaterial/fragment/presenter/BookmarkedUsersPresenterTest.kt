package me.rei_m.hbfavmaterial.fragment.presenter

import me.rei_m.hbfavmaterial.entity.ArticleEntity
import me.rei_m.hbfavmaterial.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.enum.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.service.BookmarkService
import me.rei_m.hbfavmaterial.service.UserService
import org.junit.After
import org.junit.Test
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.runners.MockitoJUnitRunner
import rx.Observable
import rx.Scheduler
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.schedulers.Schedulers
import java.util.Date
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
    fun testOnResume_initialize() {

        val bookmarkList: MutableList<BookmarkEntity> = mutableListOf()
        bookmarkList.add(createTestBookmarkEntity(0, ""))

        `when`(bookmarkService.findByArticleUrl(bookmarkEntity.articleEntity.url)).thenReturn(Observable.just(bookmarkList))

        doAnswer { Unit }.`when`(view).showUserList(bookmarkList)

        val presenter = BookmarkedUsersPresenter(bookmarkService)
        presenter.onCreate(view, bookmarkEntity, BookmarkCommentFilter.ALL)
        presenter.onResume()

        verify(view, timeout(TimeUnit.SECONDS.toMillis(1).times(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1).times(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1).times(1))).hideEmpty()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1).times(1))).showUserList(bookmarkList)
    }

    @Test
    fun testOnPause() {

    }

    @Test
    fun testOnRefreshList() {

    }

    @Test
    fun testOnOptionItemSelected() {

    }

    @Test
    fun testOnClickUser() {

    }
}