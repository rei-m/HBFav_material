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
import android.support.v4.app.Fragment
import android.view.*
import dagger.Binds
import dagger.android.AndroidInjector
import dagger.android.support.DaggerFragment
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.constant.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.databinding.FragmentBookmarkedUsersBinding
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory
import me.rei_m.hbfavmaterial.presentation.widget.adapter.UserListAdapter
import me.rei_m.hbfavmaterial.viewmodel.widget.adapter.di.UserListItemViewModelModule
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.BookmarkedUsersFragmentViewModel
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.di.BookmarkedUsersFragmentViewModelModule
import javax.inject.Inject

/**
 * 対象の記事をブックマークしているユーザの一覧を表示するFragment.
 */
class BookmarkedUsersFragment : DaggerFragment() {

    companion object {

        private const val ARG_ARTICLE_URL = "ARG_ARTICLE_URL"

        private const val KEY_BOOKMARK_COMMENT_FILTER = "KEY_BOOKMARK_COMMENT_FILTER"

        fun newInstance(articleUrl: String) = BookmarkedUsersFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ARTICLE_URL, articleUrl)
            }
        }
    }

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var viewModelFactory: BookmarkedUsersFragmentViewModel.Factory

    @Inject
    lateinit var injector: UserListAdapter.Injector

    private lateinit var binding: FragmentBookmarkedUsersBinding

    private lateinit var viewModel: BookmarkedUsersFragmentViewModel

    private lateinit var adapter: UserListAdapter

    private var disposable: CompositeDisposable? = null

    private var listener: OnFragmentInteractionListener? = null

    private val articleUrl: String by lazy {
        requireNotNull(arguments?.getString(ARG_ARTICLE_URL)) {
            "Arguments is NULL $ARG_ARTICLE_URL"
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            val bookmarkCommentFilter = BookmarkCommentFilter.values()[savedInstanceState.getInt(KEY_BOOKMARK_COMMENT_FILTER)]
            viewModelFactory.bookmarkCommentFilter = bookmarkCommentFilter
        }
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(BookmarkedUsersFragmentViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBookmarkedUsersBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        adapter = UserListAdapter(context, injector, viewModel.bookmarkUserList)
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
            navigator.navigateToOthersBookmark(it)
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
        outState.putInt(KEY_BOOKMARK_COMMENT_FILTER, viewModel.bookmarkCommentFilter.get().ordinal)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_bookmarked_users, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        item ?: return false

        val id = item.itemId

        if (id == android.R.id.home) {
            return super.onOptionsItemSelected(item)
        }

        val commentFilter = BookmarkCommentFilter.forMenuId(id)

        viewModel.bookmarkCommentFilter.set(commentFilter)

        listener?.onChangeFilter(commentFilter)

        return true
    }

    interface OnFragmentInteractionListener {
        fun onChangeFilter(bookmarkCommentFilter: BookmarkCommentFilter)
    }

    @ForFragment
    @dagger.Subcomponent(modules = arrayOf(
            BookmarkedUsersFragmentViewModelModule::class,
            UserListItemViewModelModule::class))
    interface Subcomponent : AndroidInjector<BookmarkedUsersFragment> {
        @dagger.Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<BookmarkedUsersFragment>() {

            abstract fun viewModelModule(module: BookmarkedUsersFragmentViewModelModule): Builder

            override fun seedInstance(instance: BookmarkedUsersFragment) {
                viewModelModule(BookmarkedUsersFragmentViewModelModule(instance.articleUrl))
            }
        }
    }

    @dagger.Module(subcomponents = arrayOf(Subcomponent::class))
    abstract inner class Module {
        @Binds
        @IntoMap
        @FragmentKey(BookmarkedUsersFragment::class)
        internal abstract fun bind(builder: Subcomponent.Builder): AndroidInjector.Factory<out Fragment>
    }
}
