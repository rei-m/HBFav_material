package me.rei_m.hbfavmaterial.activity

import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.view.View
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entity.ArticleEntity
import me.rei_m.hbfavmaterial.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.enum.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.fragment.BookmarkedUsersFragment
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.util.ActivityController
import java.util.*

@RunWith(RobolectricTestRunner::class)
class BookmarkedUsersActivityTest {

    private lateinit var activityController: ActivityController<BookmarkedUsersActivity>

    private lateinit var activity: BookmarkedUsersActivity

    private val fragment: BookmarkedUsersFragment
        get() = activity.supportFragmentManager.findFragmentByTag(BookmarkedUsersFragment::class.java.simpleName) as BookmarkedUsersFragment

    private val fab: FloatingActionButton
        get() = activity.findViewById(R.id.fab) as FloatingActionButton

    private fun createTestBookmarkEntity(no: Int): BookmarkEntity {
        return BookmarkEntity(
                articleEntity = ArticleEntity(
                        title = "ArticleEntity_title_$no",
                        url = "ArticleEntity_url_$no",
                        bookmarkCount = no,
                        iconUrl = "ArticleEntity_iconUrl_$no",
                        body = "ArticleEntity_body_$no",
                        bodyImageUrl = "ArticleEntity_bodyImageUrl_$no"
                ),
                description = "BookmarkEntity_description_$no",
                creator = "BookmarkEntity_creator_$no",
                date = Date(),
                bookmarkIconUrl = "BookmarkEntity_bookmarkIconUrl_$no")
    }

    private val bookmarkEntity = createTestBookmarkEntity(1)

    @Before
    fun setUp() {
        val intent = Intent(RuntimeEnvironment.application, BookmarkedUsersActivity::class.java).apply {
            putExtra("ARG_BOOKMARK", bookmarkEntity)
        }

        activityController = Robolectric.buildActivity(BookmarkedUsersActivity::class.java, intent)
        activity = activityController.create().start().get()
    }

    @Test
    fun testOnCreate() {
        assertNotNull(fragment)
        assertThat(activity.supportActionBar?.title.toString(), `is`("${bookmarkEntity.articleEntity.bookmarkCount.toString()} users - ${BookmarkCommentFilter.ALL.title(activity)}"))
        assertThat(fab.visibility, `is`(View.GONE))
    }

    @Test
    fun testOnChangeFilter() {
        activity.onChangeFilter(BookmarkCommentFilter.COMMENT)
        assertThat(activity.supportActionBar?.title.toString(), `is`("${bookmarkEntity.articleEntity.bookmarkCount.toString()} users - ${BookmarkCommentFilter.COMMENT.title(activity)}"))
    }
}