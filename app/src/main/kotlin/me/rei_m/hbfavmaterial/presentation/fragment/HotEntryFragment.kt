package me.rei_m.hbfavmaterial.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.constant.EntryTypeFilter
import me.rei_m.hbfavmaterial.databinding.FragmentHotEntryBinding
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.di.HotEntryFragmentComponent
import me.rei_m.hbfavmaterial.di.HotEntryFragmentModule
import me.rei_m.hbfavmaterial.extension.getAppContext
import me.rei_m.hbfavmaterial.presentation.view.adapter.BookmarkPagerAdapter
import me.rei_m.hbfavmaterial.presentation.view.adapter.EntryListAdapter
import me.rei_m.hbfavmaterial.presentation.viewmodel.HotEntryFragmentViewModel
import javax.inject.Inject

/**
 * HotEntryを一覧で表示するFragment.
 */
class HotEntryFragment : BaseFragment(),
        MainPageFragment {

    companion object {

        private const val ARG_PAGE_INDEX = "ARG_PAGE_INDEX"

        private const val KEY_FILTER_TYPE = "KEY_FILTER_TYPE"

        fun newInstance(pageIndex: Int): HotEntryFragment = HotEntryFragment().apply {
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
    lateinit var viewModel: HotEntryFragmentViewModel

    private lateinit var component: HotEntryFragmentComponent

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
        viewModel.entryTypeFilter = entryTypeFilter
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = FragmentHotEntryBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        val adapter = EntryListAdapter(context, component, viewModel.entryList)
        binding.listView.adapter = adapter

        return binding.root
    }

    override fun onStart() {
        super.onStart()
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

        listener?.onChangeFilter(pageTitle)

        return true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable(KEY_FILTER_TYPE, viewModel.entryTypeFilter)
    }

    @Suppress("UNCHECKED_CAST")
    override fun setupFragmentComponent() {
        component = (activity as HasComponent<Injector>).getComponent()
                .plus(HotEntryFragmentModule(this))
        component.inject(this)
    }

    interface Injector {
        fun plus(fragmentModule: HotEntryFragmentModule?): HotEntryFragmentComponent
    }

    interface OnFragmentInteractionListener {
        fun onChangeFilter(newPageTitle: String)
    }
}
