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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.MenuItem
import dagger.Binds
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import dagger.multibindings.IntoMap
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.ActivityOthersBookmarkBinding
import me.rei_m.hbfavmaterial.di.ForActivity
import me.rei_m.hbfavmaterial.extension.openUrl
import me.rei_m.hbfavmaterial.extension.setFragment
import me.rei_m.hbfavmaterial.presentation.activity.di.ActivityModule
import me.rei_m.hbfavmaterial.presentation.widget.fragment.OthersBookmarkFragment

/**
 * 他人のブックマークを表示するActivity.
 */
class OthersBookmarkActivity : DaggerAppCompatActivity() {

    companion object {

        private const val ARG_USER_ID = "ARG_USER_ID"

        fun createIntent(context: Context, userId: String): Intent {
            return Intent(context, OthersBookmarkActivity::class.java)
                    .putExtra(ARG_USER_ID, userId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityOthersBookmarkBinding>(this, R.layout.activity_others_bookmark)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val userId = intent.getStringExtra(ARG_USER_ID)
        supportActionBar?.title = userId

        binding.fab.setOnClickListener {
            val url = getString(R.string.url_web_hatena_bookmark).replace("{{0}}", userId)
            applicationContext.openUrl(url)
        }

        if (savedInstanceState == null) {
            setFragment(OthersBookmarkFragment.newInstance(userId))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId
        when (id) {
            android.R.id.home ->
                finish()
            else ->
                return super.onOptionsItemSelected(item)
        }

        return true
    }
    
    @ForActivity
    @dagger.Subcomponent(modules = arrayOf(
            ActivityModule::class,
            OthersBookmarkFragment.Module::class)
    )
    interface Subcomponent : AndroidInjector<OthersBookmarkActivity> {
        @dagger.Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<OthersBookmarkActivity>() {

            abstract fun activityModule(module: ActivityModule): Builder

            override fun seedInstance(instance: OthersBookmarkActivity) {
                activityModule(ActivityModule(instance))
            }
        }
    }

    @dagger.Module(subcomponents = arrayOf(Subcomponent::class))
    abstract inner class Module {
        @Binds
        @IntoMap
        @ActivityKey(OthersBookmarkActivity::class)
        internal abstract fun bind(builder: Subcomponent.Builder): AndroidInjector.Factory<out Activity>
    }
}
