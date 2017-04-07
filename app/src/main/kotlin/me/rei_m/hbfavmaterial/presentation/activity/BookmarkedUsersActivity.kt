package me.rei_m.hbfavmaterial.presentation.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.MenuItem
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.constant.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.databinding.ActivityBookmarkedUsersBinding
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.model.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.extension.setFragment
import me.rei_m.hbfavmaterial.presentation.activity.di.ActivityModule
import me.rei_m.hbfavmaterial.presentation.activity.di.BookmarkedUsersActivityComponent
import me.rei_m.hbfavmaterial.presentation.activity.di.BookmarkedUsersActivityModule
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkedUsersFragment

class BookmarkedUsersActivity : BaseActivity(),
        HasComponent<BookmarkedUsersActivityComponent>,
        BookmarkedUsersFragment.OnFragmentInteractionListener {

    companion object {

        private const val ARG_BOOKMARK = "ARG_BOOKMARK"

        fun createIntent(context: Context, bookmarkEntity: BookmarkEntity): Intent {
            return Intent(context, BookmarkedUsersActivity::class.java)
                    .putExtra(ARG_BOOKMARK, bookmarkEntity)
        }
    }

    private var component: BookmarkedUsersActivityComponent? = null

    private val bookmark: BookmarkEntity by lazy {
        intent.getSerializableExtra(ARG_BOOKMARK) as BookmarkEntity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityBookmarkedUsersBinding>(this, R.layout.activity_bookmarked_users)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        displayTitle(BookmarkCommentFilter.ALL)

        if (savedInstanceState == null) {
            setFragment(BookmarkedUsersFragment.newInstance(bookmark.article.url), BookmarkedUsersFragment::class.java.simpleName)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        component = null
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId

        when (id) {
            android.R.id.home ->
                finish()
            else ->
                return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onChangeFilter(bookmarkCommentFilter: BookmarkCommentFilter) {
        displayTitle(bookmarkCommentFilter)
    }

    override fun setUpActivityComponent() {
        component = createActivityComponent()
    }

    override fun getComponent(): BookmarkedUsersActivityComponent = component ?: let {
        val component = createActivityComponent()
        this@BookmarkedUsersActivity.component = component
        return@let component
    }

    private fun createActivityComponent(): BookmarkedUsersActivityComponent {
        val component = (application as App).component
                .plus(BookmarkedUsersActivityModule(), ActivityModule(this))
        component.inject(this)
        return component
    }

    private fun displayTitle(commentFilter: BookmarkCommentFilter) {
        val bookmarkCountString = bookmark.article.bookmarkCount.toString()
        supportActionBar?.title = "$bookmarkCountString users - ${commentFilter.title(applicationContext)}"
    }
}
