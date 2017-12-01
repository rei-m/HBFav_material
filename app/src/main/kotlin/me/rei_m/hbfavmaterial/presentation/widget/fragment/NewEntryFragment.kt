/*
 * Copyright (c) 2017. Rei Matsushita
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

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
import me.rei_m.hbfavmaterial.databinding.FragmentNewEntryBinding
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkPagerAdapter
import me.rei_m.hbfavmaterial.presentation.widget.adapter.EntryListAdapter
import me.rei_m.hbfavmaterial.viewmodel.widget.adapter.di.EntryListItemViewModelModule
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.NewEntryFragmentViewModel
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.di.NewEntryFragmentViewModelModule
import javax.inject.Inject

/**
 * 新着Entryを一覧で表示するFragment.
 */
class NewEntryFragment : DaggerFragment(),
        MainPageFragment {

    companion object {

        private const val ARG_PAGE_INDEX = "ARG_PAGE_INDEX"

        private const val KEY_ENTRY_TYPE_FILTER = "KEY_ENTRY_TYPE_FILTER"

        fun newInstance(pageIndex: Int) = NewEntryFragment().apply {
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
    lateinit var viewModelFactory: NewEntryFragmentViewModel.Factory

    @Inject
    lateinit var injector: EntryListAdapter.Injector

    private lateinit var binding: FragmentNewEntryBinding

    private lateinit var viewModel: NewEntryFragmentViewModel

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
        if (savedInstanceState != null) {
            val entryTypeFilter = EntryTypeFilter.values()[savedInstanceState.getInt(KEY_ENTRY_TYPE_FILTER)]
            viewModelFactory.entryTypeFilter = entryTypeFilter
        }
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(NewEntryFragmentViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewEntryBinding.inflate(inflater, container, false)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_ENTRY_TYPE_FILTER, viewModel.entryTypeFilter.get().ordinal)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_entry, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        item ?: return false

        val filter = EntryTypeFilter.forMenuId(item.itemId)

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
                NewEntryFragmentViewModelModule::class,
                EntryListItemViewModelModule::class)
        )
        internal abstract fun contributeInjector(): NewEntryFragment
    }
}
