package me.rei_m.hbfavmaterial.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.setFragment
import me.rei_m.hbfavmaterial.fragments.CreditFragment
import me.rei_m.hbfavmaterial.fragments.FromDeveloperFragment
import me.rei_m.hbfavmaterial.utils.FragmentUtil.Companion.Tag

class FrameActivity : BaseActivity() {

    companion object {

        private val ARG_TAG = "TAG"

        fun createIntent(context: Context, tag: Tag): Intent {
            return Intent(context, FrameActivity::class.java).apply {
                putExtra(ARG_TAG, tag)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val tag = intent.getSerializableExtra(ARG_TAG) as Tag

            when (tag) {
                Tag.FROM_DEVELOPER -> {
                    setFragment(FromDeveloperFragment.newInstance())
                    supportActionBar?.title = getString(R.string.fragment_title_from_developer)
                }
                Tag.CREDIT -> {
                    setFragment(CreditFragment.newInstance())
                    supportActionBar?.title = getString(R.string.fragment_title_credit)
                }
                else -> {

                }
            }
        }
        findViewById(R.id.fab).hide()
    }
}
