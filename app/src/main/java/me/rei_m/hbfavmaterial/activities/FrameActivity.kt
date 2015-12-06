package me.rei_m.hbfavmaterial.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.extensions.setFragment
import me.rei_m.hbfavmaterial.fragments.FromDeveloperFragment
import me.rei_m.hbfavmaterial.utils.FragmentUtil.Companion.Tag

public class FrameActivity : BaseActivity() {

    companion object {

        private val ARG_TAG = "TAG"

        public fun createIntent(context: Context, tag: Tag): Intent {
            val intent = Intent(context, FrameActivity::class.java)
            intent.putExtra(ARG_TAG, tag)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val tag = intent.getSerializableExtra(ARG_TAG) as Tag

            when (tag) {
                Tag.FROM_DEVELOPER -> {
                    setFragment(FromDeveloperFragment.newInstance())
                    supportActionBar.title = getString(R.string.fragment_title_from_developer)
                }
                else -> {
                }
            }
        }

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.hide()
    }
}
