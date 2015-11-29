package me.rei_m.hbfavkotlin.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.squareup.otto.Subscribe
import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.events.BookmarkClickedEvent
import me.rei_m.hbfavkotlin.events.EventBusHolder
import me.rei_m.hbfavkotlin.extensions.replaceFragment
import me.rei_m.hbfavkotlin.extensions.setFragment
import me.rei_m.hbfavkotlin.fragments.BookmarkFavoriteFragment
import me.rei_m.hbfavkotlin.fragments.BookmarkUserFragment
import me.rei_m.hbfavkotlin.fragments.EntryWebViewFragment

public class OthersBookmarkActivity : AppCompatActivity() {

    companion object {

        private val ARG_USER_ID = "ARG_USER_ID"

        public fun createIntent(context: Context, userId: String): Intent {
            val intent = Intent(context, OthersBookmarkActivity::class.java)
            intent.putExtra(ARG_USER_ID, userId)
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

        val userId = intent.getStringExtra(ARG_USER_ID)
        supportActionBar.title = userId

        if (savedInstanceState == null) {
            setFragment(BookmarkUserFragment.newInstance(userId))
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
        // EventBus登録解除
        EventBusHolder.EVENT_BUS.unregister(this)

        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId;

        when (id) {
            android.R.id.home ->
                finish();
            else ->
                return super.onOptionsItemSelected(item);
        }

        return true
    }

    @Subscribe
    @SuppressWarnings("unused")
    public fun onBookmarkClicked(event: BookmarkClickedEvent) {
//        replaceFragment(EntryWebViewFragment.newInstance(event.bookmarkEntity.link))
    }
}
