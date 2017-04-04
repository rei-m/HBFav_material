package me.rei_m.hbfavmaterial.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.constant.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.constant.FilterItem
import me.rei_m.hbfavmaterial.databinding.FragmentBookmarkedUsersBinding
import me.rei_m.hbfavmaterial.di.BookmarkedUsersFragmentComponent
import me.rei_m.hbfavmaterial.di.BookmarkedUsersFragmentModule
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.presentation.view.adapter.UserListAdapter
import me.rei_m.hbfavmaterial.presentation.viewmodel.BookmarkedUsersFragmentViewModel
import javax.inject.Inject

/**
 * 対象の記事をブックマークしているユーザの一覧を表示するFragment.
 */
class BookmarkedUsersFragment : BaseFragment() {

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

    private lateinit var component: BookmarkedUsersFragmentComponent

    private var listener: OnFragmentInteractionListener? = null

    private val articleUrl: String by lazy { arguments.getString(ARG_ARTICLE_URL) }

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

        viewModel.onCreate(articleUrl, bookmarkCommentFilter)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = FragmentBookmarkedUsersBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        val adapter = UserListAdapter(context, component, viewModel.bookmarkUserList)
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
        inflater?.inflate(R.menu.fragment_bookmarked_users, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        item ?: return false

        val id = item.itemId

        if (id == android.R.id.home) {
            return super.onOptionsItemSelected(item)
        }

        val commentFilter = FilterItem.forMenuId(id) as BookmarkCommentFilter

        viewModel.onOptionItemSelected(commentFilter)

        listener?.onChangeFilter(commentFilter)

        return true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable(KEY_FILTER_TYPE, viewModel.bookmarkCommentFilter)
    }

    @Suppress("UNCHECKED_CAST")
    override fun setupFragmentComponent() {
        component = (activity as HasComponent<Injector>).getComponent()
                .plus(BookmarkedUsersFragmentModule(this))
        component.inject(this)
    }

    interface OnFragmentInteractionListener {
        fun onChangeFilter(bookmarkCommentFilter: BookmarkCommentFilter)
    }

    interface Injector {
        fun plus(fragmentModule: BookmarkedUsersFragmentModule?): BookmarkedUsersFragmentComponent
    }
}
