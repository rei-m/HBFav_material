package me.rei_m.hbfavmaterial.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.events.ClickedEvent
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.setFragment
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.extensions.startActivityWithClearTop
import me.rei_m.hbfavmaterial.fragments.ExplainAppFragment
import me.rei_m.hbfavmaterial.utils.FragmentUtil
import me.rei_m.hbfavmaterial.views.adapters.BookmarkPagerAdaptor

public class ExplainAppActivity : BaseActivityWithDrawer() {

    companion object {
        public fun createIntent(context: Context): Intent {
            return Intent(context, ExplainAppActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById(R.id.pager).hide()
        findViewById(R.id.content).show()
        if (savedInstanceState == null) {
            setFragment(ExplainAppFragment.newInstance())
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_bookmark_favorite ->
                startActivityWithClearTop(MainActivity.createIntent(this, BookmarkPagerAdaptor.INDEX_PAGER_BOOKMARK_FAVORITE))
            R.id.nav_bookmark_own ->
                startActivityWithClearTop(MainActivity.createIntent(this, BookmarkPagerAdaptor.INDEX_PAGER_BOOKMARK_OWN))
            R.id.nav_hot_entry ->
                startActivityWithClearTop(MainActivity.createIntent(this, BookmarkPagerAdaptor.INDEX_PAGER_HOT_ENTRY))
            R.id.nav_new_entry ->
                startActivityWithClearTop(MainActivity.createIntent(this, BookmarkPagerAdaptor.INDEX_PAGER_NEW_ENTRY))
            R.id.nav_setting ->
                startActivityWithClearTop(SettingActivity.createIntent(this))
            else -> {
            }
        }

        return super.onNavigationItemSelected(item)
    }

    @Subscribe
    @SuppressWarnings("unused")
    public fun onClicked(event: ClickedEvent) {
        when (event.type) {
            ClickedEvent.Companion.Type.FROM_DEVELOPER -> {
                startActivity(FrameActivity.createIntent(this, FragmentUtil.Companion.Tag.FROM_DEVELOPER))
            }
            ClickedEvent.Companion.Type.CREDIT -> {
                startActivity(FrameActivity.createIntent(this, FragmentUtil.Companion.Tag.CREDIT))
            }
            else -> {
            }

        }
        //        startActivity(BookmarkActivity.createIntent(this, event.bookmarkEntity))
    }
}