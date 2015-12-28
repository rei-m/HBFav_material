package me.rei_m.hbfavmaterial.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.ui.BookmarkUsersFilteredEvent
import me.rei_m.hbfavmaterial.events.ui.UserListItemClickedEvent
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.setFragment
import me.rei_m.hbfavmaterial.fragments.BookmarkUsersFragment
import me.rei_m.hbfavmaterial.utils.BookmarkUtil
import me.rei_m.hbfavmaterial.utils.BookmarkUtil.Companion.FilterType

public class BookmarkUsersActivity : BaseActivity() {

    private var mBookmarkEntity: BookmarkEntity? = null

    private var mFilterType: FilterType? = null

    companion object {

        private val ARG_BOOKMARK = "ARG_BOOKMARK"

        private val KEY_FILTER_TYPE = "KEY_FILTER_TYPE"

        public fun createIntent(context: Context, bookmarkEntity: BookmarkEntity): Intent {
            return Intent(context, BookmarkUsersActivity::class.java).apply {
                putExtra(ARG_BOOKMARK, bookmarkEntity)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBookmarkEntity = intent.getSerializableExtra(ARG_BOOKMARK) as BookmarkEntity

        if (savedInstanceState == null) {
            setFragment(BookmarkUsersFragment.newInstance(mBookmarkEntity!!))
        }

        displayTitle(mFilterType ?: FilterType.ALL)

        findViewById(R.id.fab).hide()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        val filterType = savedInstanceState?.getSerializable(KEY_FILTER_TYPE)
        mFilterType = filterType?.let { it as FilterType } ?: FilterType.ALL
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable(KEY_FILTER_TYPE, mFilterType)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bookmark_users, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId;

        when (id) {
            R.id.menu_filter_users_all ->
                mFilterType = FilterType.ALL
            R.id.menu_filter_users_comment ->
                mFilterType = FilterType.COMMENT
            else ->
                return super.onOptionsItemSelected(item)
        }

        EventBusHolder.EVENT_BUS.post(BookmarkUsersFilteredEvent(mFilterType!!))

        displayTitle(mFilterType!!)

        return true
    }

    @Subscribe
    public fun subscribe(event: UserListItemClickedEvent) {
        startActivity(OthersBookmarkActivity.createIntent(this, event.bookmarkEntity.creator))
    }

    private fun displayTitle(filterType: FilterType) {
        val bookmarkCountString = mBookmarkEntity?.articleEntity?.bookmarkCount.toString()
        val filterTypeString = BookmarkUtil.getFilterTypeString(applicationContext, filterType)
        supportActionBar.title = "$bookmarkCountString users - $filterTypeString"
    }
}