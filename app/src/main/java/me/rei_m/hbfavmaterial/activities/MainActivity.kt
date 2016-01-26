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
import me.rei_m.hbfavmaterial.events.ui.BookmarkListItemClickedEvent
import me.rei_m.hbfavmaterial.events.ui.EntryCategoryChangedEvent
import me.rei_m.hbfavmaterial.events.ui.EntryListItemClickedEvent
import me.rei_m.hbfavmaterial.events.ui.MainPageDisplayEvent
import me.rei_m.hbfavmaterial.events.ui.MainPageDisplayEvent.Companion.Kind
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.extensions.startActivityWithClearTop
import me.rei_m.hbfavmaterial.models.HotEntryModel
import me.rei_m.hbfavmaterial.models.NewEntryModel
import me.rei_m.hbfavmaterial.utils.BookmarkUtil
import me.rei_m.hbfavmaterial.utils.BookmarkUtil.Companion.EntryType
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

    lateinit private var mMenu: Menu

    companion object {

        private val ARG_PAGER_INDEX = "ARG_PAGER_INDEX"

        fun createIntent(context: Context,
                         index: Int = BookmarkPagerAdaptor.INDEX_PAGER_BOOKMARK_FAVORITE): Intent {
            return Intent(context, MainActivity::class.java).apply {
                putExtra(ARG_PAGER_INDEX, index)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.graph.inject(this)

        binding.activityMainApp.pager.initialize(supportFragmentManager, this)
        binding.activityMainApp.pager.currentItem = intent.getIntExtra(ARG_PAGER_INDEX, BookmarkPagerAdaptor.INDEX_PAGER_BOOKMARK_FAVORITE)
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
            else ->
                return super.onOptionsItemSelected(item)
        }

        // イベントを飛ばしてFragment側でカテゴリに合わせた表示に切り替える
        val target = if (binding.activityMainApp.pager.currentItem === BookmarkPagerAdaptor.INDEX_PAGER_HOT_ENTRY)
            EntryCategoryChangedEvent.Companion.Target.HOT
        else
            EntryCategoryChangedEvent.Companion.Target.NEW

        EventBusHolder.EVENT_BUS.post(EntryCategoryChangedEvent(entryType, target))

        // Activityのタイトルも切り替える
        val currentPageTitle = binding.activityMainApp.pager.getCurrentPageTitle().toString()
        val entryTypeString = BookmarkUtil.getEntryTypeString(applicationContext, entryType)
        supportActionBar.title = "$currentPageTitle - $entryTypeString"

        return true
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // Drawer内のメニュー選択時のイベント

        when (item.itemId) {
            R.id.nav_bookmark_favorite ->
                binding.activityMainApp.pager.currentItem = BookmarkPagerAdaptor.INDEX_PAGER_BOOKMARK_FAVORITE
            R.id.nav_bookmark_own ->
                binding.activityMainApp.pager.currentItem = BookmarkPagerAdaptor.INDEX_PAGER_BOOKMARK_OWN
            R.id.nav_hot_entry ->
                binding.activityMainApp.pager.currentItem = BookmarkPagerAdaptor.INDEX_PAGER_HOT_ENTRY
            R.id.nav_new_entry ->
                binding.activityMainApp.pager.currentItem = BookmarkPagerAdaptor.INDEX_PAGER_NEW_ENTRY
            R.id.nav_setting ->
                startActivityWithClearTop(SettingActivity.createIntent(this))
            R.id.nav_explain_app ->
                startActivityWithClearTop(ExplainAppActivity.createIntent(this))
            else -> {
                return super.onNavigationItemSelected(item)
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

        // ページの種類に応じてActivityのタイトル表示とメニューの表示/非表示を切り替える
        val title: String
        val navItemId: Int

        when (event.kind) {
            Kind.BOOKMARK_FAVORITE -> {
                mMenu.hide()
                title = binding.activityMainApp.pager.getCurrentPageTitle().toString()
                navItemId = R.id.nav_bookmark_favorite
            }
            Kind.BOOKMARK_OWN -> {
                mMenu.hide()
                title = binding.activityMainApp.pager.getCurrentPageTitle().toString()
                navItemId = R.id.nav_bookmark_own
            }
            Kind.HOT_ENTRY -> {
                mMenu.show()
                val mainTitle = binding.activityMainApp.pager.getCurrentPageTitle().toString()
                val subTitle = BookmarkUtil.getEntryTypeString(applicationContext, hotEntryModel.entryType)
                title = "$mainTitle - $subTitle"
                navItemId = R.id.nav_hot_entry
            }
            Kind.NEW_ENTRY -> {
                mMenu.show()
                val mainTitle = binding.activityMainApp.pager.getCurrentPageTitle().toString()
                val subTitle = BookmarkUtil.getEntryTypeString(applicationContext, newEntryModel.entryType)
                title = "$mainTitle - $subTitle"
                navItemId = R.id.nav_new_entry
            }
            else ->
                return
        }

        // タイトルを切り替え、ナビゲーションView内のメニューの選択中の項目をチェック状態にする
        supportActionBar.title = title
        binding.activityMainNav.setCheckedItem(navItemId)
    }
}
