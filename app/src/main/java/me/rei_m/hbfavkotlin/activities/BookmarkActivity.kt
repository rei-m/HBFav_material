package me.rei_m.hbfavkotlin.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.fragments.BookmarkFragment
import me.rei_m.hbfavkotlin.fragments.BookmarkWebViewFragment
import me.rei_m.hbfavkotlin.entities.BookmarkEntity
import me.rei_m.hbfavkotlin.views.adapters.BookmarkPagerAdaptor

public class BookmarkActivity : AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener,
        BookmarkFragment.OnFragmentInteractionListener {

    companion object {

        private val ARG_BOOKMARK = "ARG_BOOKMARK"

        public fun createIntent(context: Context, bookmarkEntity: BookmarkEntity): Intent {
            val intent = Intent(context, BookmarkActivity::class.java)
            intent.putExtra(ARG_BOOKMARK, bookmarkEntity)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)

        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            val bookmark = intent.getSerializableExtra(ARG_BOOKMARK) as BookmarkEntity
            setFragment(BookmarkFragment.newInstance(bookmark))
        }

//        val fab = findViewById(R.id.fab) as FloatingActionButton
//        fab.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(view: View) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()
//            }
//        })

    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onClickBookmark(bookmarkEntity: BookmarkEntity) {
        replaceFragment(BookmarkWebViewFragment.newInstance(bookmarkEntity))
    }
}
