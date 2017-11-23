package me.rei_m.hbfavmaterial.presentation.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.MenuItem
import dagger.Binds
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import dagger.multibindings.IntoMap
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.constant.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.databinding.ActivityBookmarkedUsersBinding
import me.rei_m.hbfavmaterial.di.ForActivity
import me.rei_m.hbfavmaterial.extension.setFragment
import me.rei_m.hbfavmaterial.model.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.presentation.activity.di.ActivityModule
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkedUsersFragment

class BookmarkedUsersActivity : DaggerAppCompatActivity(),
        BookmarkedUsersFragment.OnFragmentInteractionListener {

    companion object {

        private const val ARG_BOOKMARK = "ARG_BOOKMARK"

        fun createIntent(context: Context, bookmarkEntity: BookmarkEntity): Intent {
            return Intent(context, BookmarkedUsersActivity::class.java)
                    .putExtra(ARG_BOOKMARK, bookmarkEntity)
        }
    }

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

    private fun displayTitle(commentFilter: BookmarkCommentFilter) {
        val bookmarkCountString = bookmark.article.bookmarkCount.toString()
        supportActionBar?.title = "$bookmarkCountString users - ${commentFilter.title(applicationContext)}"
    }

    @ForActivity
    @dagger.Subcomponent(modules = arrayOf(
            ActivityModule::class,
            BookmarkedUsersFragment.Module::class)
    )
    interface Subcomponent : AndroidInjector<BookmarkedUsersActivity> {
        @dagger.Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<BookmarkedUsersActivity>() {

            abstract fun activityModule(module: ActivityModule): Builder

            override fun seedInstance(instance: BookmarkedUsersActivity) {
                activityModule(ActivityModule(instance))
            }
        }
    }

    @dagger.Module(subcomponents = arrayOf(Subcomponent::class))
    abstract inner class Module {
        @Binds
        @IntoMap
        @ActivityKey(BookmarkedUsersActivity::class)
        internal abstract fun bind(builder: Subcomponent.Builder): AndroidInjector.Factory<out Activity>
    }
}
