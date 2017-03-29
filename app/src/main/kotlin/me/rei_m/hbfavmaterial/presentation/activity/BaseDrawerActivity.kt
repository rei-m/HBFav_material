package me.rei_m.hbfavmaterial.presentation.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.ActivityMainBinding
import me.rei_m.hbfavmaterial.presentation.viewmodel.BaseDrawerActivityViewModel
import javax.inject.Inject

/**
 * Drawer付きActivityの基底クラス.
 */
abstract class BaseDrawerActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var viewModel: BaseDrawerActivityViewModel

    protected var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        setSupportActionBar(binding.appBar.toolbar)

        val toggle = ActionBarDrawerToggle(this,
                binding.drawer,
                binding.appBar.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)

        binding.drawer.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener(this)

        binding.viewModel = viewModel

        this.binding = binding
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onBackPressed() {
        binding?.drawer?.let {
            if (it.isDrawerOpen(GravityCompat.START)) {
                it.closeDrawer(GravityCompat.START)
                return
            }
        }
        super.onBackPressed()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding?.drawer?.closeDrawer(GravityCompat.START)
        return true
    }
}
