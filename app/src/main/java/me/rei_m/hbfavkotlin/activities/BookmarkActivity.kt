package me.rei_m.hbfavkotlin.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.entities.BookmarkEntity
import me.rei_m.hbfavkotlin.entities.EntryEntity
import me.rei_m.hbfavkotlin.extensions.replaceFragment
import me.rei_m.hbfavkotlin.extensions.setFragment
import me.rei_m.hbfavkotlin.fragments.BookmarkFragment
import me.rei_m.hbfavkotlin.fragments.EntryWebViewFragment

public class BookmarkActivity : AppCompatActivity(),
        BookmarkFragment.OnFragmentInteractionListener {

    companion object {

        private val ARG_BOOKMARK = "ARG_BOOKMARK"
        private val ARG_ENTRY = "ARG_ENTRY"

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
                val bookmark = intent.getSerializableExtra(ARG_BOOKMARK) as BookmarkEntity
                setFragment(BookmarkFragment.newInstance(bookmark))
            } else {
                val entryEntity = intent.getSerializableExtra(ARG_ENTRY) as EntryEntity
                setFragment(EntryWebViewFragment.newInstance(entryEntity.link))
            }
        }

        val fab = findViewById(R.id.fab) as FloatingActionButton

        val fab2 = findViewById(R.id.fab2) as FloatingActionButton

        fab.setOnClickListener({
            val hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.hyperspace_jump)
            fab2.startAnimation(hyperspaceJumpAnimation)
        })

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

    override fun onClickBookmark(bookmarkEntity: BookmarkEntity) {
        replaceFragment(EntryWebViewFragment.newInstance(bookmarkEntity.link))
    }
}
