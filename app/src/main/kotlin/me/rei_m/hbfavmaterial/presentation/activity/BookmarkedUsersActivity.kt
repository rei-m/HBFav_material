package me.rei_m.hbfavmaterial.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.enum.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.extension.hide
import me.rei_m.hbfavmaterial.extension.setFragment
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkedUsersFragment

class BookmarkedUsersActivity : BaseSingleActivity(),
        BookmarkedUsersFragment.OnFragmentInteractionListener {

    private val bookmarkEntity: BookmarkEntity by lazy {
        intent.getSerializableExtra(ARG_BOOKMARK) as BookmarkEntity
    }

    companion object {

        private const val ARG_BOOKMARK = "ARG_BOOKMARK"

        fun createIntent(context: Context, bookmarkEntity: BookmarkEntity): Intent {
            return Intent(context, BookmarkedUsersActivity::class.java)
                    .putExtra(ARG_BOOKMARK, bookmarkEntity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            setFragment(BookmarkedUsersFragment.newInstance(bookmarkEntity), BookmarkedUsersFragment::class.java.simpleName)
        }

        displayTitle(BookmarkCommentFilter.ALL)

        findViewById(R.id.fab)?.hide()
    }

    override fun onChangeFilter(bookmarkCommentFilter: BookmarkCommentFilter) {
        displayTitle(bookmarkCommentFilter)
    }

    private fun displayTitle(commentFilter: BookmarkCommentFilter) {
        val bookmarkCountString = bookmarkEntity.articleEntity.bookmarkCount.toString()
        supportActionBar?.title = "$bookmarkCountString users - ${commentFilter.title(applicationContext)}"
    }
}
