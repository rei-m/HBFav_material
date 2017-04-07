package me.rei_m.hbfavmaterial.presentation.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ShareCompat
import android.view.Menu
import android.view.MenuItem
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.ActivityBookmarkBinding
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.model.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.model.entity.EntryEntity
import me.rei_m.hbfavmaterial.extension.replaceFragment
import me.rei_m.hbfavmaterial.extension.setFragment
import me.rei_m.hbfavmaterial.extension.subscribeBus
import me.rei_m.hbfavmaterial.presentation.activity.di.ActivityModule
import me.rei_m.hbfavmaterial.presentation.activity.di.BookmarkActivityComponent
import me.rei_m.hbfavmaterial.presentation.activity.di.BookmarkActivityModule
import me.rei_m.hbfavmaterial.presentation.event.FailToConnectionEvent
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.event.ShowArticleEvent
import me.rei_m.hbfavmaterial.presentation.event.ShowBookmarkEditEvent
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkFragment
import me.rei_m.hbfavmaterial.presentation.fragment.EditBookmarkDialogFragment
import me.rei_m.hbfavmaterial.presentation.fragment.EntryWebViewFragment
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.activity.BookmarkActivityViewModel
import javax.inject.Inject

/**
 * ブックマークの詳細を表示するActivity.
 */
class BookmarkActivity : BaseActivity(),
        HasComponent<BookmarkActivityComponent> {

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
    lateinit var rxBus: RxBus

    @Inject
    lateinit var viewModel: BookmarkActivityViewModel

    private var component: BookmarkActivityComponent? = null

    private var disposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
        disposable = CompositeDisposable()
        disposable?.add(rxBus.toObservable().subscribeBus({
            when (it) {
                is ShowArticleEvent -> {
                    replaceFragment(EntryWebViewFragment.newInstance(it.url), EntryWebViewFragment.TAG)
                }
                is ShowBookmarkEditEvent -> {
                    EditBookmarkDialogFragment
                            .newInstance(it.articleTitle, it.articleUrl)
                            .show(supportFragmentManager, EditBookmarkDialogFragment.TAG)
                }
                is FailToConnectionEvent -> {
                    showFailToConnectionMessage()
                }
            }
        }))
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
        disposable?.dispose()
        disposable = null
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        component = null
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

    override fun setUpActivityComponent() {
        component = createActivityComponent()
    }

    override fun getComponent(): BookmarkActivityComponent = component ?: let {
        val component = createActivityComponent()
        this@BookmarkActivity.component = component
        return@let component
    }

    private fun createActivityComponent(): BookmarkActivityComponent {
        val component = (application as App).component
                .plus(BookmarkActivityModule(), ActivityModule(this))
        component.inject(this)
        return component
    }

    private fun showFailToConnectionMessage() {
        Snackbar.make(findViewById(R.id.content), getString(R.string.message_error_network), Snackbar.LENGTH_SHORT).setAction("Action", null).show()
    }
}
