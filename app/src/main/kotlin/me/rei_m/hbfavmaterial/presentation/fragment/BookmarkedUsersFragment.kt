package me.rei_m.hbfavmaterial.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerFragment
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.constant.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.databinding.FragmentBookmarkedUsersBinding
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory
import me.rei_m.hbfavmaterial.presentation.widget.adapter.UserListAdapter
import me.rei_m.hbfavmaterial.viewmodel.fragment.BookmarkedUsersFragmentViewModel
import me.rei_m.hbfavmaterial.viewmodel.fragment.di.BookmarkedUsersFragmentViewModelModule
import me.rei_m.hbfavmaterial.viewmodel.widget.di.UserListItemViewModelModule
import javax.inject.Inject


/**
 * 対象の記事をブックマークしているユーザの一覧を表示するFragment.
 */
class BookmarkedUsersFragment : DaggerFragment() {

    companion object {

        private const val ARG_ARTICLE_URL = "ARG_ARTICLE_URL"

        private const val KEY_FILTER_TYPE = "KEY_FILTER_TYPE"

        fun newInstance(articleUrl: String) = BookmarkedUsersFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ARTICLE_URL, articleUrl)
            }
        }
    }

    @Inject
    lateinit var viewModel: BookmarkedUsersFragmentViewModel

    @Inject
    lateinit var injector: UserListAdapter.Injector

    private var listener: OnFragmentInteractionListener? = null

    private val articleUrl: String? by lazy { arguments?.getString(ARG_ARTICLE_URL) }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val bookmarkCommentFilter = if (savedInstanceState != null) {
            savedInstanceState.getSerializable(KEY_FILTER_TYPE) as BookmarkCommentFilter
        } else {
            BookmarkCommentFilter.ALL
        }

        viewModel.onCreate(articleUrl!!, bookmarkCommentFilter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentBookmarkedUsersBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        val adapter = UserListAdapter(context!!, injector, viewModel.bookmarkUserList)
        binding.listView.adapter = adapter

        viewModel.onCreateView(SnackbarFactory(binding.root))

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

    override fun onDestroyView() {
        viewModel.onDestroyView()
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
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

        viewModel.onOptionItemSelected(commentFilter)

        listener?.onChangeFilter(commentFilter)

        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(KEY_FILTER_TYPE, viewModel.bookmarkCommentFilter)
    }

    interface OnFragmentInteractionListener {
        fun onChangeFilter(bookmarkCommentFilter: BookmarkCommentFilter)
    }

    @dagger.Module
    abstract inner class Module {
        @ForFragment
        @ContributesAndroidInjector(modules = arrayOf(
                BookmarkedUsersFragmentViewModelModule::class,
                UserListItemViewModelModule::class)
        )
        internal abstract fun contributeInjector(): BookmarkedUsersFragment
    }
}
