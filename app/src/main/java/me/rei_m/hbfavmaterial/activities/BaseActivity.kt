package me.rei_m.hbfavmaterial.activities

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.ActivityBinding
import me.rei_m.hbfavmaterial.events.EventBusHolder

/**
 * Drawer無しのActivityの基底クラス.
 */
abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var binding: ActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity)
        setSupportActionBar(binding.toolbar)
        supportActionBar.setDisplayHomeAsUpEnabled(true)
        supportActionBar.setHomeButtonEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        EventBusHolder.EVENT_BUS.register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBusHolder.EVENT_BUS.unregister(this)
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
}
