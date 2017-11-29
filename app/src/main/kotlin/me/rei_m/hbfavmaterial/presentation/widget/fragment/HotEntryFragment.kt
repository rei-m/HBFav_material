package me.rei_m.hbfavmaterial.presentation.widget.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.view.*
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.constant.EntryTypeFilter
import me.rei_m.hbfavmaterial.databinding.FragmentHotEntryBinding
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkPagerAdapter
import me.rei_m.hbfavmaterial.presentation.widget.adapter.EntryListAdapter
import me.rei_m.hbfavmaterial.viewmodel.widget.adapter.di.EntryListItemViewModelModule
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.HotEntryFragmentViewModel
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.di.HotEntryFragmentViewModelModule
import javax.inject.Inject

/**
 * HotEntryを一覧で表示するFragment.
 */
class HotEntryFragment : DaggerFragment(),
        MainPageFragment {

    companion object {

        private const val ARG_PAGE_INDEX = "ARG_PAGE_INDEX"

        fun newInstance(pageIndex: Int) = HotEntryFragment().apply {
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
        get() = BookmarkPagerAdapter.Page.values()[pageIndex].title(appContext, viewModel.entryTypeFilter.get().title(appContext))

    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var viewModelFactory: HotEntryFragmentViewModel.Factory

    @Inject
    lateinit var injector: EntryListAdapter.Injector

    private lateinit var binding: FragmentHotEntryBinding

    private lateinit var viewModel: HotEntryFragmentViewModel

    private lateinit var adapter: EntryListAdapter

    private var disposable: CompositeDisposable? = null

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
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HotEntryFragmentViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHotEntryBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        adapter = EntryListAdapter(context, injector, viewModel.entryList)
        binding.listView.adapter = adapter

        return binding.root
    }

    override fun onDestroyView() {
        adapter.releaseCallback()
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        disposable = CompositeDisposable()
        disposable?.addAll(viewModel.onItemClickEvent.subscribe {
            navigator.navigateToBookmark(it)
        }, viewModel.onRaiseRefreshErrorEvent.subscribe {
            SnackbarFactory(binding.root).create(R.string.message_error_network).show()
        })
    }

    override fun onPause() {
        disposable?.dispose()
        disposable = null
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_entry, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        item ?: return false

        val filter = EntryTypeFilter.forMenuId(item.itemId)

        viewModel.onOptionItemSelected(filter)
        listener?.onUpdateFilter(pageIndex)

        return true
    }

    interface OnFragmentInteractionListener {
        fun onUpdateFilter(pageIndex: Int)
    }

    @dagger.Module
    abstract inner class Module {
        @ForFragment
        @ContributesAndroidInjector(modules = arrayOf(
                HotEntryFragmentViewModelModule::class,
                EntryListItemViewModelModule::class)
        )
        internal abstract fun contributeInjector(): HotEntryFragment
    }
}
