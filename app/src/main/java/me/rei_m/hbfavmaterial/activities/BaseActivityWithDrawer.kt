package me.rei_m.hbfavmaterial.activities

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.ActivityMainBinding
import me.rei_m.hbfavmaterial.databinding.NavHeaderMainBinding
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.models.UserModel
import javax.inject.Inject

/**
 * Drawer付きActivityの基底クラス.
 */
abstract class BaseActivityWithDrawer : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var userModel: UserModel

    protected lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.graph.inject(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.activityMainApp.toolbar)

        with(ActionBarDrawerToggle(this,
                binding.activityMainLayoutDrawer,
                binding.activityMainApp.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)) {
            binding.activityMainLayoutDrawer.setDrawerListener(this@with)
            syncState()
        }

        binding.activityMainNav.setNavigationItemSelectedListener(this)

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
        if (binding.activityMainLayoutDrawer.isDrawerOpen(GravityCompat.START)) {
            binding.activityMainLayoutDrawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding.activityMainLayoutDrawer.closeDrawer(GravityCompat.START)
        return true
    }

    protected fun displayUserIconAndName(id: String) {
        val navBinding = DataBindingUtil.bind<NavHeaderMainBinding>(binding.activityMainNav.getHeaderView(0))
        navBinding.userId = id
    }
}
