package me.rei_m.hbfavmaterial.presentation.activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ShareCompat
import android.view.Menu
import android.view.MenuItem
import dagger.Binds
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import dagger.multibindings.IntoMap
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.ActivityBookmarkBinding
import me.rei_m.hbfavmaterial.di.ForActivity
import me.rei_m.hbfavmaterial.extension.replaceFragment
import me.rei_m.hbfavmaterial.extension.setFragment
import me.rei_m.hbfavmaterial.model.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.model.entity.EntryEntity
import me.rei_m.hbfavmaterial.presentation.activity.di.ActivityModule
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.widget.dialog.EditBookmarkDialogFragment
import me.rei_m.hbfavmaterial.presentation.widget.fragment.BookmarkFragment
import me.rei_m.hbfavmaterial.presentation.widget.fragment.EntryWebViewFragment
import me.rei_m.hbfavmaterial.viewmodel.activity.BookmarkActivityViewModel
import me.rei_m.hbfavmaterial.viewmodel.activity.di.BookmarkActivityViewModelModule
import javax.inject.Inject

/**
 * ブックマークの詳細を表示するActivity.
 */
class BookmarkActivity : DaggerAppCompatActivity(),
        BookmarkFragment.OnFragmentInteractionListener {

    companion object {

        private const val ARG_BOOKMARK = "ARG_BOOKMARK"
        private const val ARG_ENTRY = "ARG_ENTRY"

        private const val KEY_ENTRY_TITLE = "KEY_ENTRY_TITLE"
        private const val KEY_ENTRY_LINK = "KEY_ENTRY_LINK"

        fun createIntent(context: Context, bookmarkEntity: BookmarkEntity): Intent {
            return Intent(context, BookmarkActivity::class.java)
                    .putExtra(ARG_BOOKMARK, bookmarkEntity)
        }

        fun createIntent(context: Context, entryEntity: EntryEntity): Intent {
            return Intent(context, BookmarkActivity::class.java).apply {
                putExtra(ARG_ENTRY, entryEntity)
            }
        }
    }

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var viewModelFactory: BookmarkActivityViewModel.Factory

    lateinit var viewModel: BookmarkActivityViewModel

    private var disposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(BookmarkActivityViewModel::class.java)
        
        val binding = DataBindingUtil.setContentView<ActivityBookmarkBinding>(this, R.layout.activity_bookmark)
        binding.viewModel = viewModel

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        if (savedInstanceState == null) {
            if (intent.hasExtra(ARG_BOOKMARK)) {
                val bookmarkEntity = intent.getSerializableExtra(ARG_BOOKMARK) as BookmarkEntity
                viewModel.entryTitle.set(bookmarkEntity.article.title)
                viewModel.entryLink.set(bookmarkEntity.article.url)
                setFragment(BookmarkFragment.newInstance(bookmarkEntity))
            } else {
                val entryEntity = intent.getSerializableExtra(ARG_ENTRY) as EntryEntity
                viewModel.entryTitle.set(entryEntity.article.title)
                viewModel.entryLink.set(entryEntity.article.url)
                setFragment(EntryWebViewFragment.newInstance(viewModel.entryLink.get()), EntryWebViewFragment.TAG)
            }
            supportActionBar?.title = viewModel.entryTitle.get()
        }
    }

    override fun onResume() {
        super.onResume()
        disposable = CompositeDisposable()
        disposable?.addAll(viewModel.showBookmarkEditEvent.subscribe {
            EditBookmarkDialogFragment
                    .newInstance(viewModel.entryTitle.get(), viewModel.entryLink.get())
                    .show(supportFragmentManager, EditBookmarkDialogFragment.TAG)
        }, viewModel.unauthorisedEvent.subscribe {
            navigator.navigateToOAuth()
        })
    }

    override fun onPause() {
        disposable?.dispose()
        disposable = null
        super.onPause()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        viewModel.entryTitle.set(savedInstanceState?.getString(KEY_ENTRY_TITLE) ?: "")
        viewModel.entryLink.set(savedInstanceState?.getString(KEY_ENTRY_LINK) ?: "")
        supportActionBar?.title = viewModel.entryTitle.get()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(KEY_ENTRY_TITLE, viewModel.entryTitle.get())
        outState?.putString(KEY_ENTRY_LINK, viewModel.entryLink.get())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bookmark, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId
        when (id) {
            android.R.id.home ->
                finish()
            R.id.menu_share ->
                ShareCompat.IntentBuilder.from(this)
                        .setChooserTitle("記事をシェアします")
                        .setSubject(viewModel.entryTitle.get())
                        .setText(viewModel.entryLink.get())
                        .setType("text/plain")
                        .startChooser()
            else ->
                return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onBackPressed() {
        supportFragmentManager.findFragmentByTag(EntryWebViewFragment.TAG)?.let {
            if (!(it as EntryWebViewFragment).backHistory()) {
                return
            }
        }
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null || requestCode != Navigator.REQ_CODE_OAUTH) {
            return
        }

        when (resultCode) {
            RESULT_OK -> {
                if (data.extras.getBoolean(OAuthActivity.ARG_IS_AUTHORIZE_DONE)) {
                    if (data.extras.getBoolean(OAuthActivity.ARG_AUTHORIZE_STATUS)) {
                        viewModel.onAuthoriseHatena()
                    }
                } else {
                    showFailToConnectionMessage()
                }
            }
        }
    }

    override fun onShowArticle(url: String) {
        replaceFragment(EntryWebViewFragment.newInstance(url), EntryWebViewFragment.TAG)
    }

    private fun showFailToConnectionMessage() {
        Snackbar.make(findViewById(R.id.content), getString(R.string.message_error_network), Snackbar.LENGTH_SHORT).setAction("Action", null).show()
    }

    @ForActivity
    @dagger.Subcomponent(modules = arrayOf(
            ActivityModule::class,
            BookmarkActivityViewModelModule::class,
            BookmarkFragment.Module::class,
            EditBookmarkDialogFragment.Module::class)
    )
    interface Subcomponent : AndroidInjector<BookmarkActivity> {
        @dagger.Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<BookmarkActivity>() {

            abstract fun activityModule(module: ActivityModule): Builder

            override fun seedInstance(instance: BookmarkActivity) {
                activityModule(ActivityModule(instance))
            }
        }
    }

    @dagger.Module(subcomponents = arrayOf(Subcomponent::class))
    abstract inner class Module {
        @Binds
        @IntoMap
        @ActivityKey(BookmarkActivity::class)
        internal abstract fun bind(builder: Subcomponent.Builder): AndroidInjector.Factory<out Activity>
    }
}
