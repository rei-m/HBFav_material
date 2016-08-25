package me.rei_m.hbfavmaterial.fragment

import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entity.ArticleEntity
import me.rei_m.hbfavmaterial.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.view.adapter.BookmarkListAdapter
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil
import java.util.*

@RunWith(RobolectricTestRunner::class)
class BookmarkFavoriteFragmentTest {

    lateinit var fragment: BookmarkFavoriteFragment

    private val view: View by lazy {
        fragment.view ?: throw IllegalStateException("fragment's view is Null")
    }

    private val listView: ListView
        get() = view.findViewById(R.id.fragment_list_list) as ListView

    private val layoutRefresh: SwipeRefreshLayout
        get() = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout

    private val textEmpty: TextView
        get() = view.findViewById(R.id.fragment_list_view_empty) as TextView

    private val progressBar: ProgressBar
        get() = view.findViewById(R.id.fragment_list_progress_list) as ProgressBar

    private val snackbarTextView: TextView
        get() = fragment.activity.findViewById(android.support.design.R.id.snackbar_text) as TextView

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

    @Before
    fun setUp() {

        fragment = BookmarkFavoriteFragment.newInstance(0)

        SupportFragmentTestUtil.startFragment(fragment, BookmarkedUsersFragmentTest.CustomDriverActivity::class.java)
    }

    @Test
    fun initialize() {
        assertThat(listView.visibility, `is`(View.VISIBLE))
        assertThat(layoutRefresh.visibility, `is`(View.VISIBLE))
        assertThat(textEmpty.visibility, `is`(View.GONE))
        assertThat(progressBar.visibility, `is`(View.GONE))
        assertThat(fragment.hasOptionsMenu(), `is`(false))
    }

    @Test
    fun testShowBookmarkList() {

        val bookmarkList = arrayListOf<BookmarkEntity>().apply {
            add(createTestBookmarkEntity(1))
            add(createTestBookmarkEntity(2))
            add(createTestBookmarkEntity(3))
            add(createTestBookmarkEntity(4))
        }

        fragment.showBookmarkList(bookmarkList)

        assertThat(listView.visibility, `is`(View.VISIBLE))
        assertThat(layoutRefresh.isRefreshing, `is`(false))

        val adapter = listView.adapter as BookmarkListAdapter
        assertThat(adapter.count, `is`(4))
        assertThat(adapter.getItem(0), `is`(bookmarkList[0]))
        assertThat(adapter.getItem(3), `is`(bookmarkList[3]))
    }
}
