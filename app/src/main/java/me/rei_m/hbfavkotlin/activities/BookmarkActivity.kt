package me.rei_m.hbfavkotlin.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ShareCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.squareup.otto.Subscribe
import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.entities.BookmarkEntity
import me.rei_m.hbfavkotlin.entities.EntryEntity
import me.rei_m.hbfavkotlin.events.BookmarkClickedEvent
import me.rei_m.hbfavkotlin.events.BookmarkUserClickedEvent
import me.rei_m.hbfavkotlin.events.EventBusHolder
import me.rei_m.hbfavkotlin.extensions.replaceFragment
import me.rei_m.hbfavkotlin.extensions.setFragment
import me.rei_m.hbfavkotlin.fragments.BookmarkFragment
import me.rei_m.hbfavkotlin.fragments.EntryWebViewFragment

public class BookmarkActivity : AppCompatActivity() {

    private var mEntryTitle: String = ""
    private var mEntryLink: String = ""

    companion object {

        private val ARG_BOOKMARK = "ARG_BOOKMARK"
        private val ARG_ENTRY = "ARG_ENTRY"

        private val KEY_ENTRY_TITLE = "KEY_ENTRY_TITLE"
        private val KEY_ENTRY_LINK = "KEY_ENTRY_LINK"

        public fun createIntent(context: Context, bookmarkEntity: BookmarkEntity): Intent {
            val intent = Intent(context, BookmarkActivity::class.java)
            intent.putExtra(ARG_BOOKMARK, bookmarkEntity)
            return intent
        }

        public fun createIntent(context: Context, entryEntity: EntryEntity): Intent {
            val intent = Intent(context, BookmarkActivity::class.java)
            intent.putExtra(ARG_ENTRY, entryEntity)
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

        if (savedInstanceState == null) {
            if (intent.hasExtra(ARG_BOOKMARK)) {
                val bookmarkEntity = intent.getSerializableExtra(ARG_BOOKMARK) as BookmarkEntity
                mEntryTitle = bookmarkEntity.title
                mEntryLink = bookmarkEntity.link
                setFragment(BookmarkFragment.newInstance(bookmarkEntity))
            } else {
                val entryEntity = intent.getSerializableExtra(ARG_ENTRY) as EntryEntity
                mEntryTitle = entryEntity.title
                mEntryLink = entryEntity.link
                setFragment(EntryWebViewFragment.newInstance(entryEntity.link))
            }
            supportActionBar.title = mEntryTitle
        }

        val fab = findViewById(R.id.fab) as FloatingActionButton

        fab.setOnClickListener({
            // はてぶ投稿ボタンとして
            //            val hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.hyperspace_jump)
            //            fab2.startAnimation(hyperspaceJumpAnimation)
        })

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

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        mEntryTitle = savedInstanceState?.getString(KEY_ENTRY_TITLE)!!
        mEntryLink = savedInstanceState?.getString(KEY_ENTRY_LINK)!!
        supportActionBar.title = mEntryTitle
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(KEY_ENTRY_TITLE, mEntryTitle)
        outState?.putString(KEY_ENTRY_LINK, mEntryLink)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bookmark, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId;

        when (id) {
            android.R.id.home ->
                finish();
            R.id.menu_share -> {
                ShareCompat.IntentBuilder.from(this)
                        .setChooserTitle("記事をシェアします")
                        .setSubject(mEntryTitle)
                        .setText(mEntryLink)
                        .setType("text/plain")
                        .startChooser()
            }
            else ->
                return super.onOptionsItemSelected(item);
        }

        return true
    }

    @Subscribe
    @SuppressWarnings("unused")
    public fun onBookmarkUserClicked(event: BookmarkUserClickedEvent) {
        startActivity(OthersBookmarkActivity.createIntent(this, event.userId))
    }

    @Subscribe
    @SuppressWarnings("unused")
    public fun onBookmarkClicked(event: BookmarkClickedEvent) {
        replaceFragment(EntryWebViewFragment.newInstance(event.bookmarkEntity.link))
    }
}
