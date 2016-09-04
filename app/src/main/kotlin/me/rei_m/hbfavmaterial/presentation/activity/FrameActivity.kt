package me.rei_m.hbfavmaterial.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.extension.hide
import me.rei_m.hbfavmaterial.extension.setFragment
import me.rei_m.hbfavmaterial.presentation.fragment.CreditFragment
import me.rei_m.hbfavmaterial.presentation.fragment.FromDeveloperFragment

class FrameActivity : BaseSingleActivity() {

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
        if (savedInstanceState == null) {
            val tag = intent.getSerializableExtra(ARG_TAG) as Tag
            setFragment(tag.newInstance())
            supportActionBar?.title = getString(tag.titleStringId)
        }
        findViewById(R.id.fab)?.hide()
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
