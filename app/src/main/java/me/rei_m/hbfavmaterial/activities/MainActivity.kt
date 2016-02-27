package me.rei_m.hbfavmaterial.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.ui.*
import me.rei_m.hbfavmaterial.extensions.startActivityWithClearTop
import me.rei_m.hbfavmaterial.models.HotEntryModel
import me.rei_m.hbfavmaterial.models.NewEntryModel
import me.rei_m.hbfavmaterial.utils.BookmarkUtil
import me.rei_m.hbfavmaterial.utils.BookmarkUtil.Companion.EntryType
import me.rei_m.hbfavmaterial.utils.BookmarkUtil.Companion.ReadAfterType
import me.rei_m.hbfavmaterial.views.adapters.BookmarkPagerAdaptor
import javax.inject.Inject

/**
 * メインActivity.
 */
class MainActivity : BaseActivityWithDrawer() {

    @Inject
    lateinit var hotEntryModel: HotEntryModel

    @Inject
    lateinit var newEntryModel: NewEntryModel

    private var mMenu: Menu? = null

    companion object {

        private val ARG_PAGER_INDEX = "ARG_PAGER_INDEX"

        fun createIntent(context: Context,
                         index: Int = BookmarkPagerAdaptor.BookmarkPage.BOOKMARK_FAVORITE.index): Intent {
            return Intent(context, MainActivity::class.java)
                    .putExtra(ARG_PAGER_INDEX, index)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.graph.inject(this)

        binding.activityMainApp.pager.initialize(supportFragmentManager)
        binding.activityMainApp.pager.currentItem = intent.getIntExtra(ARG_PAGER_INDEX, BookmarkPagerAdaptor.BookmarkPage.BOOKMARK_FAVORITE.index)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        mMenu = menu

        binding.activityMainApp.pager.postCurrentPageDisplayEvent()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId;

        val entryType: EntryType

        // 人気エントリー/新着エントリー表示時のカテゴリ切り替え
        when (id) {
            R.id.menu_category_all ->
                entryType = EntryType.ALL
            R.id.menu_category_world ->
                entryType = EntryType.WORLD
            R.id.menu_category_politics_and_economy ->
                entryType = EntryType.POLITICS_AND_ECONOMY
            R.id.menu_category_life ->
                entryType = EntryType.LIFE
            R.id.menu_category_entertainment ->
                entryType = EntryType.ENTERTAINMENT
            R.id.menu_category_study ->
                entryType = EntryType.STUDY
            R.id.menu_category_technology ->
                entryType = EntryType.TECHNOLOGY
            R.id.menu_category_animation_and_game ->
                entryType = EntryType.ANIMATION_AND_GAME
            R.id.menu_category_comedy ->
                entryType = EntryType.COMEDY
            R.id.menu_filter_bookmark_read_after ->
                return onOptionBookmarkFilterSelected(item)
            R.id.menu_filter_bookmark_all ->
                return onOptionBookmarkFilterSelected(item)
            else ->
                return super.onOptionsItemSelected(item)
        }

        // イベントを飛ばしてFragment側でカテゴリに合わせた表示に切り替える
        val target = if (binding.activityMainApp.pager.currentItem === BookmarkPagerAdaptor.BookmarkPage.HOT_ENTRY.index)
            EntryCategoryChangedEvent.Companion.Target.HOT
        else
            EntryCategoryChangedEvent.Companion.Target.NEW

        EventBusHolder.EVENT_BUS.post(EntryCategoryChangedEvent(entryType, target))

        // Activityのタイトルも切り替える
        val page = BookmarkPagerAdaptor.BookmarkPage.values()[binding.activityMainApp.pager.currentItem]
        val entryTypeString = BookmarkUtil.getEntryTypeString(applicationContext, entryType)
        supportActionBar?.title = page.title(applicationContext, entryTypeString)

        return true
    }

    private fun onOptionBookmarkFilterSelected(item: MenuItem?): Boolean {

        val id = item?.itemId;

        val subTitle: String

        when (id) {
            R.id.menu_filter_bookmark_read_after -> {
                EventBusHolder.EVENT_BUS.post(ReadAfterFilterChangedEvent(ReadAfterType.AFTER_READ))
                subTitle = getString(R.string.text_read_after)
            }
            R.id.menu_filter_bookmark_all -> {
                EventBusHolder.EVENT_BUS.post(ReadAfterFilterChangedEvent(ReadAfterType.ALL))
                subTitle = getString(R.string.filter_bookmark_users_all)
            }
            else -> {
                subTitle = ""
            }
        }
        val page = BookmarkPagerAdaptor.BookmarkPage.values()[binding.activityMainApp.pager.currentItem]

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
            else ->
                startActivityWithClearTop(MainActivity.createIntent(this, BookmarkPagerAdaptor.BookmarkPage.forMenuId(item.itemId).index))
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
            BookmarkPagerAdaptor.BookmarkPage.BOOKMARK_FAVORITE -> {
                ""
            }
            BookmarkPagerAdaptor.BookmarkPage.BOOKMARK_OWN -> {
                ""
            }
            BookmarkPagerAdaptor.BookmarkPage.HOT_ENTRY -> {
                BookmarkUtil.getEntryTypeString(applicationContext, hotEntryModel.entryType)
            }
            BookmarkPagerAdaptor.BookmarkPage.NEW_ENTRY -> {
                BookmarkUtil.getEntryTypeString(applicationContext, newEntryModel.entryType)
            }
        }

        // タイトルを切り替え、ナビゲーションView内のメニューの選択中の項目をチェック状態にする
        supportActionBar?.title = event.page.title(applicationContext, subTitle)
        event.page.toggleMenu(menu)
        binding.activityMainNav.setCheckedItem(event.page.menuId)
    }
}
