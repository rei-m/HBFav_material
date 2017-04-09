package me.rei_m.hbfavmaterial.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.constant.EntryTypeFilter
import me.rei_m.hbfavmaterial.databinding.FragmentNewEntryBinding
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.extension.getAppContext
import me.rei_m.hbfavmaterial.presentation.fragment.di.NewEntryFragmentComponent
import me.rei_m.hbfavmaterial.presentation.fragment.di.NewEntryFragmentModule
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkPagerAdapter
import me.rei_m.hbfavmaterial.presentation.widget.adapter.EntryListAdapter
import me.rei_m.hbfavmaterial.viewmodel.fragment.NewEntryFragmentViewModel
import javax.inject.Inject

/**
 * 新着Entryを一覧で表示するFragment.
 */
class NewEntryFragment : BaseFragment(),
        MainPageFragment {

    companion object {

        private const val ARG_PAGE_INDEX = "ARG_PAGE_INDEX"

        private const val KEY_FILTER_TYPE = "KEY_FILTER_TYPE"

        fun newInstance(pageIndex: Int) = NewEntryFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_PAGE_INDEX, pageIndex)
            }
        }
    }

    override val pageIndex: Int
        get() = arguments.getInt(ARG_PAGE_INDEX)

    override val pageTitle: String
        get() = BookmarkPagerAdapter.Page.values()[pageIndex].title(getAppContext(), viewModel.entryTypeFilter.title(getAppContext()))

    @Inject
    lateinit var viewModel: NewEntryFragmentViewModel

    private lateinit var component: NewEntryFragmentComponent

    private var disposable: CompositeDisposable? = null

    private var listener: OnFragmentInteractionListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val entryTypeFilter = if (savedInstanceState != null) {
            savedInstanceState.getSerializable(KEY_FILTER_TYPE) as EntryTypeFilter
        } else {
            EntryTypeFilter.ALL
        }
        viewModel.onCreate(entryTypeFilter)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = FragmentNewEntryBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        val adapter = EntryListAdapter(context, component, viewModel.entryList)
        binding.listView.adapter = adapter

        viewModel.onCreateView(SnackbarFactory(binding.root))

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        disposable = CompositeDisposable()
        disposable?.addAll(viewModel.updateFilterEvent.subscribe {
            listener?.onUpdateFilter(pageIndex)
        })
        viewModel.onStart()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
        disposable?.dispose()
        disposable = null
    }

    override fun onDestroyView() {
        viewModel.onDestroyView()
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_entry, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        item ?: return false

        val filter = EntryTypeFilter.forMenuId(item.itemId)

        viewModel.onOptionItemSelected(filter)

        return true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable(KEY_FILTER_TYPE, viewModel.entryTypeFilter)
    }

    @Suppress("UNCHECKED_CAST")
    override fun setupFragmentComponent() {
        component = (activity as HasComponent<Injector>).getComponent()
                .plus(NewEntryFragmentModule())
        component.inject(this)
    }

    interface OnFragmentInteractionListener {
        fun onUpdateFilter(pageIndex: Int)
    }

    interface Injector {
        fun plus(fragmentModule: NewEntryFragmentModule?): NewEntryFragmentComponent
    }
}
