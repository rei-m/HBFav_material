package me.rei_m.hbfavmaterial.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.enums.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.enums.FilterItemI
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.ui.BookmarkUsersFilteredEvent
import me.rei_m.hbfavmaterial.events.ui.UserListItemClickedEvent
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.setFragment
import me.rei_m.hbfavmaterial.fragments.BookmarkUsersFragment

class BookmarkUsersActivity : BaseSingleActivity() {

    private val mBookmarkEntity: BookmarkEntity by lazy {
        intent.getSerializableExtra(ARG_BOOKMARK) as BookmarkEntity
    }

    private var mCommentFilter: BookmarkCommentFilter = BookmarkCommentFilter.ALL

    companion object {

        private val ARG_BOOKMARK = "ARG_BOOKMARK"

        private val KEY_FILTER_TYPE = "KEY_FILTER_TYPE"

        fun createIntent(context: Context, bookmarkEntity: BookmarkEntity): Intent {
            return Intent(context, BookmarkUsersActivity::class.java)
                    .putExtra(ARG_BOOKMARK, bookmarkEntity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            setFragment(BookmarkUsersFragment.newInstance(mBookmarkEntity))
        }

        displayTitle(mCommentFilter)

        findViewById(R.id.fab)?.hide()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        val filterType = savedInstanceState?.getSerializable(KEY_FILTER_TYPE)
        mCommentFilter = filterType?.let { it as BookmarkCommentFilter } ?: BookmarkCommentFilter.ALL
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable(KEY_FILTER_TYPE, mCommentFilter)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bookmark_users, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        item ?: return false

        val id = item.itemId;

        if (id == android.R.id.home) {
            return super.onOptionsItemSelected(item)
        }
        mCommentFilter = FilterItemI.forMenuId(id) as BookmarkCommentFilter

        EventBusHolder.EVENT_BUS.post(BookmarkUsersFilteredEvent(mCommentFilter))

        displayTitle(mCommentFilter)

        return true
    }

    @Subscribe
    fun subscribe(event: UserListItemClickedEvent) {
        startActivity(OthersBookmarkActivity.createIntent(this, event.bookmarkEntity.creator))
    }

    private fun displayTitle(commentFilter: BookmarkCommentFilter) {
        val bookmarkCountString = mBookmarkEntity.articleEntity.bookmarkCount.toString()
        supportActionBar?.title = "$bookmarkCountString users - ${commentFilter.title(applicationContext)}"
    }
}
