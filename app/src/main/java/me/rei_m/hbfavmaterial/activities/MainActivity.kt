package me.rei_m.hbfavmaterial.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.events.*
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.managers.ModelLocator
import me.rei_m.hbfavmaterial.models.HotEntryModel
import me.rei_m.hbfavmaterial.models.NewEntryModel
import me.rei_m.hbfavmaterial.utils.BookmarkUtil
import me.rei_m.hbfavmaterial.utils.BookmarkUtil.Companion.EntryType
import me.rei_m.hbfavmaterial.views.adapters.BookmarkPagerAdaptor
import me.rei_m.hbfavmaterial.views.widgets.manager.BookmarkViewPager
import me.rei_m.hbfavmaterial.events.BookmarkPageDisplayEvent.Companion.Kind as PageKind

public class MainActivity : AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener {

    private var mMenu: Menu? = null

    companion object {
        public fun createIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout

        val toggle = ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)

        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        val pager = findViewById(R.id.pager) as BookmarkViewPager
        pager.init(supportFragmentManager, this)
    }

    override fun onResume() {
        super.onResume()

        // EventBus登録
        EventBusHolder.EVENT_BUS.register(this)
    }

    override fun onPause() {
        super.onPause()

        // EventBus登録解除
        EventBusHolder.EVENT_BUS.unregister(this)
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
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

        viewPager.currentItem = when (item.itemId) {
            R.id.nav_bookmark_favorite ->
                BookmarkPagerAdaptor.INDEX_PAGER_BOOKMARK_FAVORITE
            R.id.nav_bookmark_own ->
                BookmarkPagerAdaptor.INDEX_PAGER_BOOKMARK_OWN
            R.id.nav_hot_entry ->
                BookmarkPagerAdaptor.INDEX_PAGER_HOT_ENTRY
            R.id.nav_new_entry ->
                BookmarkPagerAdaptor.INDEX_PAGER_NEW_ENTRY
            else ->
                BookmarkPagerAdaptor.INDEX_PAGER_BOOKMARK_FAVORITE
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout

        drawer.closeDrawer(GravityCompat.START)

        return true
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
