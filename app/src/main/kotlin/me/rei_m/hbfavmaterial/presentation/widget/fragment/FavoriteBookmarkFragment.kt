package me.rei_m.hbfavmaterial.presentation.widget.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.FragmentFavoriteBookmarkBinding
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkListAdapter
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkPagerAdapter
import me.rei_m.hbfavmaterial.viewmodel.widget.adapter.di.BookmarkListItemViewModelModule
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.FavoriteBookmarkFragmentViewModel
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.di.FavoriteBookmarkFragmentViewModelModule
import javax.inject.Inject

/**
 * お気に入りのブックマークを一覧で表示するFragment.
 */
class FavoriteBookmarkFragment : DaggerFragment(),
        MainPageFragment {

    companion object {

        private const val ARG_PAGE_INDEX = "ARG_PAGE_INDEX"

        fun newInstance(pageIndex: Int) = FavoriteBookmarkFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_PAGE_INDEX, pageIndex)
            }
        }
    }

    override val pageIndex: Int by lazy {
        requireNotNull(arguments?.getInt(ARG_PAGE_INDEX)) {
            "Arguments is NULL $ARG_PAGE_INDEX"
        }
    }

    override val pageTitle: String by lazy {
        BookmarkPagerAdapter.Page.values()[pageIndex].title(appContext, "")
    }

    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var viewModelFactory: FavoriteBookmarkFragmentViewModel.Factory

    @Inject
    lateinit var injector: BookmarkListAdapter.Injector

    private lateinit var viewModel: FavoriteBookmarkFragmentViewModel

    private lateinit var binding: FragmentFavoriteBookmarkBinding

    private lateinit var adapter: BookmarkListAdapter

    private var disposable: CompositeDisposable? = null

    private var footerView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FavoriteBookmarkFragmentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFavoriteBookmarkBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        adapter = BookmarkListAdapter(context!!, injector, viewModel.bookmarkList)
        binding.listView.adapter = adapter

        return binding.root
    }

    override fun onDestroyView() {
        adapter.releaseCallback()
        footerView = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        disposable = CompositeDisposable()
        disposable?.addAll(viewModel.hasNextPageUpdatedEvent.subscribe {
            if (it) {
                if (footerView == null) {
                    footerView = View.inflate(binding.listView.context, R.layout.list_fotter_loading, null)
                    binding.listView.addFooterView(footerView)
                }
            } else {
                if (footerView != null) {
                    binding.listView.removeFooterView(footerView)
                    footerView = null
                }
            }
        }, viewModel.onItemClickEvent.subscribe {
            navigator.navigateToBookmark(it)
        }, viewModel.onRaiseGetNextPageErrorEvent.subscribe {
            SnackbarFactory(binding.root).create(R.string.message_error_network).show()
        }, viewModel.onRaiseRefreshErrorEvent.subscribe {
            SnackbarFactory(binding.root).create(R.string.message_error_network).show()
        })
    }

    override fun onPause() {
        disposable?.dispose()
        disposable = null
        super.onPause()
    }

    @dagger.Module
    abstract inner class Module {
        @ForFragment
        @ContributesAndroidInjector(modules = arrayOf(
                FavoriteBookmarkFragmentViewModelModule::class,
                BookmarkListItemViewModelModule::class)
        )
        internal abstract fun contributeInjector(): FavoriteBookmarkFragment
    }
}
