package me.rei_m.hbfavmaterial.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.Menu
import android.view.MenuItem
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.events.*
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.extensions.startActivityWithClearTop
import me.rei_m.hbfavmaterial.managers.ModelLocator
import me.rei_m.hbfavmaterial.models.HotEntryModel
import me.rei_m.hbfavmaterial.models.NewEntryModel
import me.rei_m.hbfavmaterial.utils.BookmarkUtil
import me.rei_m.hbfavmaterial.utils.BookmarkUtil.Companion.EntryType
import me.rei_m.hbfavmaterial.views.adapters.BookmarkPagerAdaptor
import me.rei_m.hbfavmaterial.views.widgets.manager.BookmarkViewPager
import me.rei_m.hbfavmaterial.events.BookmarkPageDisplayEvent.Companion.Kind as PageKind

public class MainActivity : BaseActivityWithDrawer() {

    private var mMenu: Menu? = null

    companion object {

        private val ARG_PAGER_INDEX = "ARG_PAGER_INDEX"

        public fun createIntent(context: Context): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(ARG_PAGER_INDEX, BookmarkPagerAdaptor.INDEX_PAGER_BOOKMARK_FAVORITE)
            return intent
        }

        public fun createIntent(context: Context,
                                index: Int): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(ARG_PAGER_INDEX, index)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pager = findViewById(R.id.pager) as BookmarkViewPager
        pager.init(supportFragmentManager, this)
        pager.currentItem = intent.getIntExtra(ARG_PAGER_INDEX, BookmarkPagerAdaptor.INDEX_PAGER_BOOKMARK_FAVORITE)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        mMenu = menu

        val pager = findViewById(R.id.pager) as BookmarkViewPager

        pager.postCurrentPageDisplayEvent()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId;

        val entryType: EntryType

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

        val viewPager = findViewById(R.id.pager) as BookmarkViewPager

        val target =
                if (viewPager.currentItem === BookmarkPagerAdaptor.INDEX_PAGER_HOT_ENTRY)
                    EntryCategoryChangedEvent.Companion.Target.HOT
                else
                    EntryCategoryChangedEvent.Companion.Target.NEW

        EventBusHolder.EVENT_BUS.post(EntryCategoryChangedEvent(entryType, target))

        val currentPageTitle = viewPager.getCurrentPageTitle().toString()
        val entryTypeString = BookmarkUtil.getEntryTypeString(applicationContext, entryType)
        supportActionBar.title = "$currentPageTitle - $entryTypeString"

        return true
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        val viewPager = findViewById(R.id.pager) as BookmarkViewPager

        when (item.itemId) {
            R.id.nav_bookmark_favorite ->
                viewPager.currentItem = BookmarkPagerAdaptor.INDEX_PAGER_BOOKMARK_FAVORITE
            R.id.nav_bookmark_own ->
                viewPager.currentItem = BookmarkPagerAdaptor.INDEX_PAGER_BOOKMARK_OWN
            R.id.nav_hot_entry ->
                viewPager.currentItem = BookmarkPagerAdaptor.INDEX_PAGER_HOT_ENTRY
            R.id.nav_new_entry ->
                viewPager.currentItem = BookmarkPagerAdaptor.INDEX_PAGER_NEW_ENTRY
            R.id.nav_setting ->
                startActivityWithClearTop(SettingActivity.createIntent(this))
            else ->
                viewPager.currentItem = BookmarkPagerAdaptor.INDEX_PAGER_BOOKMARK_FAVORITE
        }

        return super.onNavigationItemSelected(item)
    }

    @Subscribe
    @SuppressWarnings("unused")
    public fun onBookmarkListItemClicked(event: BookmarkListItemClickedEvent) {
        startActivity(BookmarkActivity.createIntent(this, event.bookmarkEntity))
    }

    @Subscribe
    @SuppressWarnings("unused")
    public fun onEntryListItemClicked(event: EntryListItemClickedEvent) {
        startActivity(BookmarkActivity.createIntent(this, event.entryEntity))
    }

    @Subscribe
    @SuppressWarnings("unused")
    public fun onBookmarkPageDisplay(event: BookmarkPageDisplayEvent) {

        val pager = findViewById(R.id.pager) as BookmarkViewPager

        val navigationView = findViewById(R.id.nav_view) as NavigationView

        val title: String
        val navItemId: Int

        when (event.kind) {
            PageKind.BOOKMARK_FAVORITE -> {
                mMenu?.hide()
                title = pager.getCurrentPageTitle().toString()
                navItemId = R.id.nav_bookmark_favorite
            }
            PageKind.BOOKMARK_OWN -> {
                mMenu?.hide()
                title = pager.getCurrentPageTitle().toString()
                navItemId = R.id.nav_bookmark_own
            }
            PageKind.HOT_ENTRY -> {
                mMenu?.show()
                val mainTitle = pager.getCurrentPageTitle().toString()
                val hotEntryModel = ModelLocator.get(ModelLocator.Companion.Tag.HOT_ENTRY) as HotEntryModel
                val subTitle = BookmarkUtil.getEntryTypeString(applicationContext, hotEntryModel.entryType)
                title = "$mainTitle - $subTitle"
                navItemId = R.id.nav_hot_entry
            }
            PageKind.NEW_ENTRY -> {
                mMenu?.show()
                val mainTitle = pager.getCurrentPageTitle().toString()
                val newEntryModel = ModelLocator.get(ModelLocator.Companion.Tag.NEW_ENTRY) as NewEntryModel
                val subTitle = BookmarkUtil.getEntryTypeString(applicationContext, newEntryModel.entryType)
                title = "$mainTitle - $subTitle"
                navItemId = R.id.nav_new_entry
            }
            else ->
                return
        }

        supportActionBar.title = title
        navigationView.setCheckedItem(navItemId)
    }
}
