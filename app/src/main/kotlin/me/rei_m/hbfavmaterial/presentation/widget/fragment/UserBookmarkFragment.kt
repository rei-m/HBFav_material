package me.rei_m.hbfavmaterial.presentation.widget.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.view.*
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.constant.ReadAfterFilter
import me.rei_m.hbfavmaterial.databinding.FragmentUserBookmarkBinding
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkListAdapter
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkPagerAdapter
import me.rei_m.hbfavmaterial.viewmodel.widget.adapter.di.BookmarkListItemViewModelModule
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.UserBookmarkFragmentViewModel
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.di.UserBookmarkFragmentViewModelModule
import javax.inject.Inject

/**
 * 自分のブックマークを一覧で表示するFragment.
 */
class UserBookmarkFragment : DaggerFragment(), MainPageFragment {

    companion object {

        private const val ARG_PAGE_INDEX = "ARG_PAGE_INDEX"

        private const val KEY_READ_AFTER_FILTER = "KEY_READ_AFTER_FILTER"

        /**
         * 自分のブックマークを表示する
         *
         * @return Fragment
         */
        fun newInstance(pageIndex: Int) = UserBookmarkFragment().apply {
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

    override val pageTitle: String
        get() = BookmarkPagerAdapter.Page.values()[pageIndex].title(appContext, viewModel.readAfterFilter.get().title(appContext))

    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var viewModelFactory: UserBookmarkFragmentViewModel.Factory

    @Inject
    lateinit var injector: BookmarkListAdapter.Injector

    private lateinit var binding: FragmentUserBookmarkBinding

    private lateinit var viewModel: UserBookmarkFragmentViewModel

    private lateinit var adapter: BookmarkListAdapter

    private var disposable: CompositeDisposable? = null

    private var footerView: View? = null

    private var listener: OnFragmentInteractionListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            val readAfterFilter = ReadAfterFilter.values()[savedInstanceState.getInt(KEY_READ_AFTER_FILTER)]
            viewModelFactory.readAfterFilter = readAfterFilter
        }
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserBookmarkFragmentViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUserBookmarkBinding.inflate(inflater, container, false)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_READ_AFTER_FILTER, viewModel.readAfterFilter.get().ordinal)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_bookmark_user, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        item ?: return false

        val filter = ReadAfterFilter.forMenuId(item.itemId)

        viewModel.onOptionItemSelected(filter)
        listener?.onUpdateFilter(pageTitle)

        return true
    }

    interface OnFragmentInteractionListener {
        fun onUpdateFilter(pageTitle: String)
    }

    @dagger.Module
    abstract inner class Module {
        @ForFragment
        @ContributesAndroidInjector(modules = arrayOf(
                UserBookmarkFragmentViewModelModule::class,
                BookmarkListItemViewModelModule::class)
        )
        internal abstract fun contributeInjector(): UserBookmarkFragment
    }
}
