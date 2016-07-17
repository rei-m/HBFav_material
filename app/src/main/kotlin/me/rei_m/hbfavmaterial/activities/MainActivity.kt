package me.rei_m.hbfavmaterial.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.Menu
import android.view.MenuItem
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.enums.EntryTypeFilter
import me.rei_m.hbfavmaterial.enums.FilterItemI
import me.rei_m.hbfavmaterial.enums.ReadAfterFilter
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.ui.*
import me.rei_m.hbfavmaterial.extensions.startActivityWithClearTop
import me.rei_m.hbfavmaterial.models.HotEntryModel
import me.rei_m.hbfavmaterial.models.NewEntryModel
import me.rei_m.hbfavmaterial.views.adapters.BookmarkPagerAdaptor
import me.rei_m.hbfavmaterial.views.widgets.manager.BookmarkViewPager
import javax.inject.Inject

/**
 * メインActivity.
 */
class MainActivity : BaseDrawerActivity() {

    @Inject
    lateinit var hotEntryModel: HotEntryModel

    @Inject
    lateinit var newEntryModel: NewEntryModel

    private var mMenu: Menu? = null

    companion object {

        private val ARG_PAGER_INDEX = "ARG_PAGER_INDEX"

        fun createIntent(context: Context,
                         index: Int = BookmarkPagerAdaptor.Page.BOOKMARK_FAVORITE.index): Intent {
            return Intent(context, MainActivity::class.java)
                    .putExtra(ARG_PAGER_INDEX, index)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)

        with(findViewById(R.id.pager) as BookmarkViewPager) {
            initialize(supportFragmentManager)
            currentItem = intent.getIntExtra(ARG_PAGER_INDEX, BookmarkPagerAdaptor.Page.BOOKMARK_FAVORITE.index)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        mMenu = menu

        with(findViewById(R.id.pager) as BookmarkViewPager) {
            postCurrentPageDisplayEvent()
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        item ?: return false

        val id = item.itemId;

        if (id == android.R.id.home) {
            return super.onOptionsItemSelected(item)
        }

        val viewPager = findViewById(R.id.pager) as BookmarkViewPager

        val filterType = FilterItemI.forMenuId(id)

        // イベントを飛ばしてFragment側でカテゴリに合わせた表示に切り替える
        when (filterType) {
            is ReadAfterFilter -> {
                EventBusHolder.EVENT_BUS.post(ReadAfterFilterChangedEvent(filterType))
            }
            is EntryTypeFilter -> {
                val target = if (viewPager.currentItem === BookmarkPagerAdaptor.Page.HOT_ENTRY.index)
                    EntryCategoryChangedEvent.Target.HOT
                else
                    EntryCategoryChangedEvent.Target.NEW
                EventBusHolder.EVENT_BUS.post(EntryCategoryChangedEvent(filterType, target))
            }
        }

        // Activityのタイトルも切り替える
        val page = BookmarkPagerAdaptor.Page.values()[viewPager.currentItem]
        val subTitle = filterType.title(applicationContext)
        supportActionBar?.title = page.title(applicationContext, subTitle)

        return true
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // Drawer内のメニュー選択時のイベント
        when (item.itemId) {
            R.id.nav_setting ->
                startActivityWithClearTop(SettingActivity.createIntent(this))
            R.id.nav_explain_app ->
                startActivityWithClearTop(ExplainAppActivity.createIntent(this))
            else -> {
                with(findViewById(R.id.pager) as BookmarkViewPager) {
                    currentItem = BookmarkPagerAdaptor.Page.forMenuId(item.itemId).index
                }
            }
        }

        return super.onNavigationItemSelected(item)
    }

    /**
     * ブックマークリスト内のアイテムクリック時のイベント
     */
    @Subscribe
    fun subscribe(event: BookmarkListItemClickedEvent) {
        startActivity(BookmarkActivity.createIntent(this, event.bookmarkEntity))
    }

    /**
     * エントリーリスト内のアイテムクリック時のイベント
     */
    @Subscribe
    fun subscribe(event: EntryListItemClickedEvent) {
        startActivity(BookmarkActivity.createIntent(this, event.entryEntity))
    }

    /**
     * ページ表示時のイベント
     */
    @Subscribe
    fun subscribe(event: MainPageDisplayEvent) {

        val menu = mMenu ?: return

        val subTitle = when (event.page) {
            BookmarkPagerAdaptor.Page.HOT_ENTRY ->
                hotEntryModel.entryType.title(applicationContext)
            BookmarkPagerAdaptor.Page.NEW_ENTRY ->
                newEntryModel.entryType.title(applicationContext)
            else ->
                ""
        }

        // タイトルを切り替え、ナビゲーションView内のメニューの選択中の項目をチェック状態にする
        supportActionBar?.title = event.page.title(applicationContext, subTitle)
        event.page.toggleMenu(menu)

        with(findViewById(R.id.activity_main_nav) as NavigationView) {
            setCheckedItem(event.page.navId)
        }
    }
}
