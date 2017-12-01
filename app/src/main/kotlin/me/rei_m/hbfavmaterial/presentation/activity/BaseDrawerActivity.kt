/*
 * Copyright (c) 2017. Rei Matsushita
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package me.rei_m.hbfavmaterial.presentation.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import dagger.android.support.DaggerAppCompatActivity
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.ActivityMainBinding
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkPagerAdapter
import me.rei_m.hbfavmaterial.viewmodel.activity.BaseDrawerActivityViewModel
import javax.inject.Inject

/**
 * Drawer付きActivityの基底クラス.
 */
abstract class BaseDrawerActivity : DaggerAppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var viewModelFactory: BaseDrawerActivityViewModel.Factory

    protected var binding: ActivityMainBinding? = null

    protected lateinit var viewModel: BaseDrawerActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = provideViewModel()

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        setSupportActionBar(binding.appBar?.toolbar)

        val toggle = ActionBarDrawerToggle(this,
                binding.drawer,
                binding.appBar?.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)

        binding.drawer.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener(this)

        binding.viewModel = viewModel

        this.binding = binding
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

    abstract fun provideViewModel(): BaseDrawerActivityViewModel

    fun onNavigationMainSelected(page: BookmarkPagerAdapter.Page) {
        navigator.navigateToMain(page)
        finish()
    }

    fun onNavigationSettingSelected() {
        navigator.navigateToSetting()
        finish()
    }

    fun onNavigationExplainAppSelected() {
        navigator.navigateToExplainApp()
        finish()
    }
}
