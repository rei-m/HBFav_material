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
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.Binds
import dagger.android.AndroidInjector
import dagger.android.support.DaggerFragment
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.FragmentOthersBookmarkBinding
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkListAdapter
import me.rei_m.hbfavmaterial.viewmodel.widget.adapter.di.BookmarkListItemViewModelModule
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.OthersBookmarkFragmentViewModel
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.di.OthersBookmarkFragmentViewModelModule
import javax.inject.Inject

/**
 * 特定のユーザーのブックマークを一覧で表示するFragment.
 */
class OthersBookmarkFragment : DaggerFragment() {

    companion object {

        private const val ARG_USER_ID = "ARG_USER_ID"

        /**
         * 他人のブックマークを表示する
         *
         * @userId: 表示対象のユーザーのID.
         * @return Fragment
         */
        fun newInstance(userId: String) = OthersBookmarkFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_USER_ID, userId)
            }
        }
    }

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var viewModelFactory: OthersBookmarkFragmentViewModel.Factory

    @Inject
    lateinit var injector: BookmarkListAdapter.Injector

    private lateinit var binding: FragmentOthersBookmarkBinding

    private lateinit var viewModel: OthersBookmarkFragmentViewModel

    private lateinit var adapter: BookmarkListAdapter

    private var disposable: CompositeDisposable? = null

    private var footerView: View? = null

    private val userId: String by lazy {
        requireNotNull(arguments?.getString(ARG_USER_ID)) {
            "Arguments is NULL $ARG_USER_ID"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(OthersBookmarkFragmentViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentOthersBookmarkBinding.inflate(inflater, container, false)
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

    @ForFragment
    @dagger.Subcomponent(modules = arrayOf(
            OthersBookmarkFragmentViewModelModule::class,
            BookmarkListItemViewModelModule::class))
    interface Subcomponent : AndroidInjector<OthersBookmarkFragment> {
        @dagger.Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<OthersBookmarkFragment>() {

            abstract fun viewModelModule(module: OthersBookmarkFragmentViewModelModule): Builder

            override fun seedInstance(instance: OthersBookmarkFragment) {
                viewModelModule(OthersBookmarkFragmentViewModelModule(instance.userId))
            }
        }
    }

    @dagger.Module(subcomponents = arrayOf(Subcomponent::class))
    abstract inner class Module {
        @Binds
        @IntoMap
        @FragmentKey(OthersBookmarkFragment::class)
        internal abstract fun bind(builder: Subcomponent.Builder): AndroidInjector.Factory<out Fragment>
    }
}
