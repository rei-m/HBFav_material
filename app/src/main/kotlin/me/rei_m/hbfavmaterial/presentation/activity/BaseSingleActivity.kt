package me.rei_m.hbfavmaterial.presentation.activity

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import me.rei_m.hbfavmaterial.R

/**
 * Drawer無しのActivityの基底クラス.
 */
abstract class BaseSingleActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)
        val toolbar = findViewById(R.id.activity_toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
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
