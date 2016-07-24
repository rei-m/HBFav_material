package me.rei_m.hbfavmaterial.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ShareCompat
import android.view.Menu
import android.view.MenuItem
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.BookmarkEditEntity
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.entities.EntryEntity
import me.rei_m.hbfavmaterial.events.ui.BookmarkClickedEvent
import me.rei_m.hbfavmaterial.events.ui.BookmarkCountClickedEvent
import me.rei_m.hbfavmaterial.events.ui.BookmarkUserClickedEvent
import me.rei_m.hbfavmaterial.extensions.replaceFragment
import me.rei_m.hbfavmaterial.extensions.setFragment
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.fragments.BookmarkFragment
import me.rei_m.hbfavmaterial.fragments.EditBookmarkDialogFragment
import me.rei_m.hbfavmaterial.fragments.EntryWebViewFragment
import me.rei_m.hbfavmaterial.repositories.HatenaTokenRepository
import me.rei_m.hbfavmaterial.service.HatenaService
import me.rei_m.hbfavmaterial.utils.ConstantUtil
import retrofit2.adapter.rxjava.HttpException
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.HttpURLConnection
import javax.inject.Inject

/**
 * ブックマークの詳細を表示するActivity.
 */
class BookmarkActivity : BaseSingleActivity() {

    companion object {

        private val ARG_BOOKMARK = "ARG_BOOKMARK"
        private val ARG_ENTRY = "ARG_ENTRY"

        private val KEY_ENTRY_TITLE = "KEY_ENTRY_TITLE"
        private val KEY_ENTRY_LINK = "KEY_ENTRY_LINK"

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
    lateinit var hatenaTokenRepository: HatenaTokenRepository

    @Inject
    lateinit var hatenaService: HatenaService

    private var subscription: Subscription? = null

    private var isLoading = false

    private var entryTitle: String = ""

    private var entryLink: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)

        if (savedInstanceState == null) {
            if (intent.hasExtra(ARG_BOOKMARK)) {
                val bookmarkEntity = intent.getSerializableExtra(ARG_BOOKMARK) as BookmarkEntity
                entryTitle = bookmarkEntity.articleEntity.title
                entryLink = bookmarkEntity.articleEntity.url
                setFragment(BookmarkFragment.newInstance(bookmarkEntity))
            } else {
                val entryEntity = intent.getSerializableExtra(ARG_ENTRY) as EntryEntity
                entryTitle = entryEntity.articleEntity.title
                entryLink = entryEntity.articleEntity.url
                setFragment(EntryWebViewFragment.newInstance(entryLink), EntryWebViewFragment.TAG)
            }
            supportActionBar?.title = entryTitle
        }

        val fab = findViewById(R.id.fab) as FloatingActionButton

        fab.setOnClickListener {
            // はてぶ投稿ボタン
            if (!hatenaTokenRepository.resolve().isAuthorised) {
                startActivityForResult(OAuthActivity.createIntent(this), ConstantUtil.REQ_CODE_OAUTH)
            } else {
                fetchBookmark(entryLink)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isLoading = false
    }

    override fun onStop() {
        super.onStop()
        subscription?.unsubscribe()
        subscription = null
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        entryTitle = savedInstanceState?.getString(KEY_ENTRY_TITLE) ?: ""
        entryLink = savedInstanceState?.getString(KEY_ENTRY_LINK) ?: ""
        supportActionBar?.title = entryTitle
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(KEY_ENTRY_TITLE, entryTitle)
        outState?.putString(KEY_ENTRY_LINK, entryLink)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bookmark, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId;

        when (id) {
            R.id.menu_share ->
                ShareCompat.IntentBuilder.from(this)
                        .setChooserTitle("記事をシェアします")
                        .setSubject(entryTitle)
                        .setText(entryLink)
                        .setType("text/plain")
                        .startChooser()
            else ->
                return super.onOptionsItemSelected(item);
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

        data ?: return

        if (requestCode != ConstantUtil.REQ_CODE_OAUTH) {
            return
        }

        when (resultCode) {
            RESULT_OK -> {
                if (data.extras.getBoolean(OAuthActivity.ARG_IS_AUTHORIZE_DONE)) {
                    if (data.extras.getBoolean(OAuthActivity.ARG_AUTHORIZE_STATUS)) {
                        fetchBookmark(entryLink)
                    }
                } else {
                    showSnackbarNetworkError(findViewById(R.id.activity_layout))
                }
            }
            else -> {

            }
        }
    }

    private fun fetchBookmark(url: String) {

        val observer = object : Observer<BookmarkEditEntity> {

            override fun onNext(t: BookmarkEditEntity) {
                EditBookmarkDialogFragment
                        .newInstance(entryTitle, entryLink, t)
                        .show(supportFragmentManager, EditBookmarkDialogFragment.TAG)
            }

            override fun onCompleted() {

            }

            override fun onError(e: Throwable?) {
                if (e is HttpException) {
                    if (e.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                        EditBookmarkDialogFragment
                                .newInstance(entryTitle, entryLink)
                                .show(supportFragmentManager, EditBookmarkDialogFragment.TAG)
                        return
                    }
                }
                showSnackbarNetworkError(findViewById(R.id.content))
            }
        }

        val oAuthTokenEntity = hatenaTokenRepository.resolve()
        subscription = hatenaService.findBookmarkByUrl(oAuthTokenEntity, url)
                .doOnUnsubscribe { isLoading = false }
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }

    @Subscribe
    fun subscribe(event: BookmarkUserClickedEvent) {
        startActivity(OthersBookmarkActivity.createIntent(this, event.userId))
    }

    @Subscribe
    fun subscribe(event: BookmarkClickedEvent) {
        replaceFragment(EntryWebViewFragment.newInstance(event.bookmarkEntity.articleEntity.url), EntryWebViewFragment.TAG)
    }

    @Subscribe
    fun subscribe(event: BookmarkCountClickedEvent) {
        startActivity(BookmarkedUsersActivity.createIntent(this, event.bookmarkEntity))
    }
}
