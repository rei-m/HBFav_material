package me.rei_m.hbfavmaterial.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.FragmentFavoriteBookmarkBinding
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.extension.getAppContext
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkListAdapter
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkPagerAdapter
import me.rei_m.hbfavmaterial.viewmodel.fragment.FavoriteBookmarkFragmentViewModel
import me.rei_m.hbfavmaterial.viewmodel.fragment.di.FavoriteBookmarkFragmentViewModelModule
import me.rei_m.hbfavmaterial.viewmodel.widget.di.BookmarkListItemViewModelModule
import javax.inject.Inject

/**
 * お気に入りのブックマークを一覧で表示するFragment.
 */
class FavoriteBookmarkFragment : DaggerFragment(),
        MainPageFragment {

    companion object {

        private const val ARG_PAGE_INDEX = "ARG_PAGE_INDEX"

        fun newInstance(pageIndex: Int) = FavoriteBookmarkFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_PAGE_INDEX, pageIndex)
            }
        }
    }

    override val pageIndex: Int
        get() = arguments!!.getInt(ARG_PAGE_INDEX)

    override val pageTitle: String
        get() = BookmarkPagerAdapter.Page.values()[pageIndex].title(getAppContext()!!, "")

    @Inject
    lateinit var viewModel: FavoriteBookmarkFragmentViewModel

    @Inject
    lateinit var injector: BookmarkListAdapter.Injector

    private var binding: FragmentFavoriteBookmarkBinding? = null

    private var disposable: CompositeDisposable? = null

    private var footerView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentFavoriteBookmarkBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        val adapter = BookmarkListAdapter(context!!, injector, viewModel.bookmarkList)
        binding.listView.adapter = adapter

        footerView = View.inflate(binding.listView.context, R.layout.list_fotter_loading, null)
        binding.listView.addFooterView(footerView)

        viewModel.onCreateView(SnackbarFactory(binding.root))

        this.binding = binding

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        disposable = CompositeDisposable()
        disposable?.addAll(viewModel.readAllItemEvent.subscribe {
            binding?.listView?.removeFooterView(footerView)
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
        footerView = null
        binding = null
        super.onDestroyView()
    }

    @dagger.Module
    abstract inner class Module {
        @ForFragment
        @ContributesAndroidInjector(modules = arrayOf(
                FavoriteBookmarkFragmentViewModelModule::class,
                BookmarkListItemViewModelModule::class)
        )
        internal abstract fun contributeInjector(): FavoriteBookmarkFragment
    }
}
