package me.rei_m.hbfavmaterial.fragment

import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.widget.ListView
import android.widget.TextView
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entity.ArticleEntity
import me.rei_m.hbfavmaterial.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.testutil.DriverActivity
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil
import java.util.*

@RunWith(RobolectricTestRunner::class)
class BookmarkedUsersFragmentTest {

    lateinit var fragment: BookmarkedUsersFragment

    private val view: View by lazy {
        fragment.view ?: throw IllegalStateException("fragment's view is Null")
    }

    private val listView: ListView
        get() = view.findViewById(R.id.fragment_list_list) as ListView

    private val layoutRefresh: SwipeRefreshLayout
        get() = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout

    private val textEmpty: TextView
        get() = view.findViewById(R.id.fragment_list_view_empty) as TextView

    private val now = Date()

    private val bookmarkEntity = BookmarkEntity(
            articleEntity = ArticleEntity(
                    title = "ArticleEntity_title",
                    url = "ArticleEntity_url",
                    bookmarkCount = 1,
                    iconUrl = "ArticleEntity_iconUrl",
                    body = "ArticleEntity_body",
                    bodyImageUrl = "ArticleEntity_bodyImageUrl"
            ),
            description = "BookmarkEntity_description",
            creator = "BookmarkEntity_creator",
            date = now,
            bookmarkIconUrl = "BookmarkEntity_bookmarkIconUrl"
    )

    @Before
    fun setUp() {

        fragment = BookmarkedUsersFragment.newInstance(bookmarkEntity)

        SupportFragmentTestUtil.startFragment(fragment, DriverActivity::class.java)
    }

    @Test
    fun initialize() {
        assertThat(listView.visibility, `is`(View.VISIBLE))
        assertThat(layoutRefresh.visibility, `is`(View.VISIBLE))
        assertThat(textEmpty.visibility, `is`(View.GONE))
    }
}