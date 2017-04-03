package me.rei_m.hbfavmaterial.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewPager
import android.view.MenuItem
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.ActivityModule
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.di.MainActivityComponent
import me.rei_m.hbfavmaterial.di.MainActivityModule
import me.rei_m.hbfavmaterial.extension.subscribeBus
import me.rei_m.hbfavmaterial.presentation.event.FailToConnectionEvent
import me.rei_m.hbfavmaterial.presentation.event.FinishActivityEvent
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkUserFragment
import me.rei_m.hbfavmaterial.presentation.fragment.HotEntryFragment
import me.rei_m.hbfavmaterial.presentation.fragment.MainPageFragment
import me.rei_m.hbfavmaterial.presentation.fragment.NewEntryFragment
import me.rei_m.hbfavmaterial.presentation.view.adapter.BookmarkPagerAdapter
import me.rei_m.hbfavmaterial.presentation.view.widget.viewpager.BookmarkViewPager
import javax.inject.Inject

/**
 * メインActivity.
 */
class MainActivity : BaseDrawerActivity(),
        HasComponent<MainActivityComponent>,
        BookmarkUserFragment.OnFragmentInteractionListener,
        HotEntryFragment.OnFragmentInteractionListener,
        NewEntryFragment.OnFragmentInteractionListener {

    companion object {

        private const val ARG_PAGER_INDEX = "ARG_PAGER_INDEX"

        fun createIntent(context: Context, page: BookmarkPagerAdapter.Page): Intent {
            return Intent(context, MainActivity::class.java)
                    .putExtra(ARG_PAGER_INDEX, page.index)
        }
    }

    @Inject
    lateinit var rxBus: RxBus

    private lateinit var component: MainActivityComponent

    private var disposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentPagerIndex = intent.getIntExtra(ARG_PAGER_INDEX, BookmarkPagerAdapter.Page.BOOKMARK_FAVORITE.index)

        val currentPage = BookmarkPagerAdapter.Page.values()[currentPagerIndex]

        supportActionBar?.title = currentPage.title(applicationContext, "")

        binding?.appBar?.pager.let {
            it as BookmarkViewPager
            it.initialize(supportFragmentManager)
            it.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    if (supportFragmentManager.fragments == null) {
                        return
                    }

                    for (fragment in supportFragmentManager.fragments) {
                        fragment as MainPageFragment
                        if (fragment.pageIndex == position) {
                            supportActionBar?.title = fragment.pageTitle
                            break
                        }
                    }
                }
            })
        }

        viewModel.onNavigationPageSelected(currentPage)
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
        disposable = CompositeDisposable()
        disposable?.add(rxBus.toObservable().subscribeBus({
            when (it) {
                is FinishActivityEvent -> {
                    finish()
                }
                is FailToConnectionEvent -> {
                    showFailToConnectionMessage()
                }
            }
        }))
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
        disposable?.dispose()
        disposable = null
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_setting -> {
                viewModel.onNavigationSettingSelected()
            }
            R.id.nav_explain_app -> {
                viewModel.onNavigationExplainAppSelected()
            }
            else -> {
                viewModel.onNavigationPageSelected(BookmarkPagerAdapter.Page.forMenuId(item.itemId))
            }
        }

        return super.onNavigationItemSelected(item)
    }

    override fun onChangeFilter(newPageTitle: String) {
        supportActionBar?.title = newPageTitle
    }

    override fun setupActivityComponent() {
        component = (application as App).component
                .plus(MainActivityModule(), ActivityModule(this))
        component.inject(this)
    }

    override fun getComponent(): MainActivityComponent {
        return component
    }

    private fun showFailToConnectionMessage() {
        Snackbar.make(findViewById(R.id.content), getString(R.string.message_error_network), Snackbar.LENGTH_SHORT).setAction("Action", null).show()
    }
}
