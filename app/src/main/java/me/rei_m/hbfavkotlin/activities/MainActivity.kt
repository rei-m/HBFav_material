package me.rei_m.hbfavkotlin.activities

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
import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.events.BookmarkListItemClickedEvent
import me.rei_m.hbfavkotlin.events.BookmarkPageDisplayEvent
import me.rei_m.hbfavkotlin.events.EntryListItemClickedEvent
import me.rei_m.hbfavkotlin.events.EventBusHolder
import me.rei_m.hbfavkotlin.extensions.hide
import me.rei_m.hbfavkotlin.extensions.show
import me.rei_m.hbfavkotlin.views.adapters.BookmarkPagerAdaptor
import me.rei_m.hbfavkotlin.views.widgets.manager.BookmarkViewPager
import me.rei_m.hbfavkotlin.events.BookmarkPageDisplayEvent.Companion.Kind as PageKind

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
        // EventBus登録解除
        EventBusHolder.EVENT_BUS.unregister(this)

        super.onPause()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        //        if (id == R.id.action_settings) {
        //            return true
        //        }

        return super.onOptionsItemSelected(item)
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

        supportActionBar.title = pager.getCurrentPageTitle()

        when (event.kind) {
            PageKind.BOOKMARK_FAVORITE -> {
                mMenu?.hide()
                navigationView.setCheckedItem(R.id.nav_bookmark_favorite)
            }
            PageKind.BOOKMARK_OWN -> {
                mMenu?.hide()
                navigationView.setCheckedItem(R.id.nav_bookmark_own)
            }
            PageKind.HOT_ENTRY -> {
                mMenu?.show()
                navigationView.setCheckedItem(R.id.nav_hot_entry)
            }
            PageKind.NEW_ENTRY -> {
                mMenu?.show()
                navigationView.setCheckedItem(R.id.nav_new_entry)
            }
        }
    }
}
