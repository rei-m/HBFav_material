package me.rei_m.hbfavmaterial.presentation.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.ActivityOthersBookmarkBinding
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.extension.openUrl
import me.rei_m.hbfavmaterial.extension.setFragment
import me.rei_m.hbfavmaterial.presentation.activity.di.ActivityModule
import me.rei_m.hbfavmaterial.presentation.activity.di.OthersBookmarkActivityComponent
import me.rei_m.hbfavmaterial.presentation.activity.di.OthersBookmarkActivityModule
import me.rei_m.hbfavmaterial.presentation.fragment.UserBookmarkFragment

/**
 * 他人のブックマークを表示するActivity.
 */
class OthersBookmarkActivity : BaseActivity(),
        HasComponent<OthersBookmarkActivityComponent> {

    companion object {

        private const val ARG_USER_ID = "ARG_USER_ID"

        fun createIntent(context: Context, userId: String): Intent {
            return Intent(context, OthersBookmarkActivity::class.java)
                    .putExtra(ARG_USER_ID, userId)
        }
    }

    private var component: OthersBookmarkActivityComponent? = null

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
            setFragment(UserBookmarkFragment.newInstance(userId))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        component = null
    }

    override fun setUpActivityComponent() {
        component = createActivityComponent()
    }

    override fun getComponent(): OthersBookmarkActivityComponent = component ?: let {
        val component = createActivityComponent()
        this@OthersBookmarkActivity.component = component
        return@let component
    }

    private fun createActivityComponent(): OthersBookmarkActivityComponent {
        val component = (application as App).component
                .plus(OthersBookmarkActivityModule(), ActivityModule(this))
        component.inject(this)
        return component
    }
}
