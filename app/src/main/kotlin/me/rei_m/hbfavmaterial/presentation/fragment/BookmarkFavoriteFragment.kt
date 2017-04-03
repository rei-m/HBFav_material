package me.rei_m.hbfavmaterial.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.rei_m.hbfavmaterial.databinding.FragmentBookmarkFavoriteBinding
import me.rei_m.hbfavmaterial.di.BookmarkFavoriteFragmentComponent
import me.rei_m.hbfavmaterial.di.BookmarkFavoriteFragmentModule
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.extension.getAppContext
import me.rei_m.hbfavmaterial.presentation.view.adapter.BookmarkListAdapter
import me.rei_m.hbfavmaterial.presentation.view.adapter.BookmarkPagerAdapter
import me.rei_m.hbfavmaterial.presentation.viewmodel.BookmarkFavoriteFragmentViewModel
import javax.inject.Inject

/**
 * お気に入りのブックマークを一覧で表示するFragment.
 */
class BookmarkFavoriteFragment : BaseFragment(),
        MainPageFragment {

    companion object {

        private const val ARG_PAGE_INDEX = "ARG_PAGE_INDEX"

        fun newInstance(pageIndex: Int) = BookmarkFavoriteFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_PAGE_INDEX, pageIndex)
            }
        }
    }

    override val pageIndex: Int
        get() = arguments.getInt(ARG_PAGE_INDEX)

    override val pageTitle: String
        get() = BookmarkPagerAdapter.Page.values()[pageIndex].title(getAppContext(), "")

    @Inject
    lateinit var viewModel: BookmarkFavoriteFragmentViewModel

    private lateinit var component: BookmarkFavoriteFragmentComponent

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = FragmentBookmarkFavoriteBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        val adapter = BookmarkListAdapter(context, component, viewModel.bookmarkList)
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

    @Suppress("UNCHECKED_CAST")
    override fun setupFragmentComponent() {
        component = (activity as HasComponent<Injector>).getComponent()
                .plus(BookmarkFavoriteFragmentModule(this))
        component.inject(this)
    }

    interface Injector {
        fun plus(fragmentModule: BookmarkFavoriteFragmentModule?): BookmarkFavoriteFragmentComponent
    }
}
