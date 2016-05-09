package me.rei_m.hbfavmaterial.activities

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.squareup.picasso.Picasso
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.models.UserModel
import me.rei_m.hbfavmaterial.utils.BookmarkUtil
import me.rei_m.hbfavmaterial.views.widgets.graphics.RoundedTransformation
import javax.inject.Inject

/**
 * Drawer付きActivityの基底クラス.
 */
abstract class BaseActivityWithDrawer : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var userModel: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.graph.inject(this)

        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val drawer = findViewById(R.id.activity_main_layout_drawer) as DrawerLayout

        val toggle = ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)

        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.activity_main_nav) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        displayUserIconAndName(userModel.userEntity?.id ?: "")
    }

    override fun onResume() {
        super.onResume()
        EventBusHolder.EVENT_BUS.register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBusHolder.EVENT_BUS.unregister(this)
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.activity_main_layout_drawer) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawer = findViewById(R.id.activity_main_layout_drawer) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    protected fun displayUserIconAndName(id: String) {
        val navigationView = findViewById(R.id.activity_main_nav) as NavigationView

        val headerView = navigationView.getHeaderView(0)
        val imageOwnerIcon = headerView.findViewById(R.id.nav_header_main_image_owner_icon) as AppCompatImageView

        Picasso.with(this)
                .load(BookmarkUtil.getLargeIconImageUrlFromId(id))
                .resizeDimen(R.dimen.icon_size_nav_crop, R.dimen.icon_size_nav_crop).centerCrop()
                .transform(RoundedTransformation())
                .into(imageOwnerIcon)

        val textOwnerId = headerView.findViewById(R.id.nav_header_main_text_owner_name) as AppCompatTextView
        textOwnerId.text = id
    }
}
