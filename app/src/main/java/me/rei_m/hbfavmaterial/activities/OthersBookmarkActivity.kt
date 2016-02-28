package me.rei_m.hbfavmaterial.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.events.ui.BookmarkListItemClickedEvent
import me.rei_m.hbfavmaterial.extensions.openUrl
import me.rei_m.hbfavmaterial.extensions.setFragment
import me.rei_m.hbfavmaterial.fragments.BookmarkUserFragment

/**
 * 他人のブックマークを表示するActivity.
 */
class OthersBookmarkActivity : BaseActivity() {

    companion object {

        private val ARG_USER_ID = "ARG_USER_ID"

        fun createIntent(context: Context, userId: String): Intent {
            return Intent(context, OthersBookmarkActivity::class.java)
                    .putExtra(ARG_USER_ID, userId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getStringExtra(ARG_USER_ID)
        supportActionBar?.title = userId

        with(findViewById(R.id.fab) as FloatingActionButton) {
            setImageResource(R.drawable.ic_add_white_24dp)
            setOnClickListener {
                val url = getString(R.string.url_web_hatena_bookmark).replace("{{0}}", userId)
                applicationContext.openUrl(url)
            }
        }
        
        if (savedInstanceState == null) {
            setFragment(BookmarkUserFragment.newInstance(userId))
        }
    }

    @Subscribe
    fun subscribe(event: BookmarkListItemClickedEvent) {
        startActivity(BookmarkActivity.createIntent(this, event.bookmarkEntity))
    }
}
