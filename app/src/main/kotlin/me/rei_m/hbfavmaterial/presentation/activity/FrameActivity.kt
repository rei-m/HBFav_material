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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.extension.setFragment
import me.rei_m.hbfavmaterial.presentation.widget.fragment.CreditFragment
import me.rei_m.hbfavmaterial.presentation.widget.fragment.FromDeveloperFragment

class FrameActivity : AppCompatActivity() {

    companion object {

        private const val ARG_TAG = "TAG"

        fun createIntent(context: Context, tag: Tag): Intent {
            return Intent(context, FrameActivity::class.java).apply {
                putExtra(ARG_TAG, tag)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)
        val toolbar = findViewById<Toolbar>(R.id.activity_toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        if (savedInstanceState == null) {
            val tag = intent.getSerializableExtra(ARG_TAG) as Tag
            setFragment(tag.newInstance())
            supportActionBar?.title = getString(tag.titleStringId)
        }
        findViewById<FloatingActionButton>(R.id.fab)?.hide()
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

    enum class Tag {
        FROM_DEVELOPER {
            override val titleStringId: Int = R.string.fragment_title_from_developer
            override fun newInstance(): Fragment = FromDeveloperFragment.newInstance()
        },
        CREDIT {
            override val titleStringId: Int = R.string.fragment_title_credit
            override fun newInstance(): Fragment = CreditFragment.newInstance()
        };

        abstract val titleStringId: Int

        abstract fun newInstance(): Fragment
    }
}
