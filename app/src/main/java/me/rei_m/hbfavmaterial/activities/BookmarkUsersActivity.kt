package me.rei_m.hbfavmaterial.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.events.BookmarkUsersFilteredEvent
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.UserListItemClickedEvent
import me.rei_m.hbfavmaterial.extensions.setFragment
import me.rei_m.hbfavmaterial.fragments.BookmarkUsersFragment
import me.rei_m.hbfavmaterial.utils.BookmarkUtil

public class BookmarkUsersActivity : AppCompatActivity() {

    companion object {

        private val ARG_BOOKMARK = "ARG_BOOKMARK"

        public fun createIntent(context: Context, bookmarkEntity: BookmarkEntity): Intent {
            val intent = Intent(context, BookmarkUsersActivity::class.java)
            intent.putExtra(ARG_BOOKMARK, bookmarkEntity)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar.setDisplayHomeAsUpEnabled(true)
        supportActionBar.setHomeButtonEnabled(true)

        val bookmarkEntity = intent.getSerializableExtra(ARG_BOOKMARK) as BookmarkEntity
        supportActionBar.title = bookmarkEntity.bookmarkCount.toString() + " users"

        if (savedInstanceState == null) {
            setFragment(BookmarkUsersFragment.newInstance(bookmarkEntity))
        }

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.hide()
    }

    override fun onResume() {
        super.onResume()

        // EventBus登録
        EventBusHolder.EVENT_BUS.register(this)
    }

    override fun onPause() {
        super.onPause()

        // EventBus登録解除
        EventBusHolder.EVENT_BUS.unregister(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bookmark_users, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId;

        when (id) {
            android.R.id.home ->
                finish();
            R.id.menu_filter_users_all -> {
                EventBusHolder.EVENT_BUS.post(BookmarkUsersFilteredEvent(BookmarkUtil.Companion.FilterType.ALL))
            }
            R.id.menu_filter_users_comment -> {
                EventBusHolder.EVENT_BUS.post(BookmarkUsersFilteredEvent(BookmarkUtil.Companion.FilterType.COMMENT))
            }
            else ->
                return super.onOptionsItemSelected(item);
        }

        return true
    }

    @Subscribe
    @SuppressWarnings("unused")
    public fun onUserListItemClicked(event: UserListItemClickedEvent) {
        startActivity(OthersBookmarkActivity.createIntent(this, event.bookmarkEntity.creator))
    }

    private fun displayTitle() {

    }
}