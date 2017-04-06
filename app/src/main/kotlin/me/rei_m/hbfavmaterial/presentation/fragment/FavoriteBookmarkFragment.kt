package me.rei_m.hbfavmaterial.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.FragmentFavoriteBookmarkBinding
import me.rei_m.hbfavmaterial.di.BookmarkFavoriteFragmentComponent
import me.rei_m.hbfavmaterial.di.BookmarkFavoriteFragmentModule
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.extension.getAppContext
import me.rei_m.hbfavmaterial.extension.subscribeBus
import me.rei_m.hbfavmaterial.presentation.event.ReadAllListItemEvent
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkListAdapter
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkPagerAdapter
import me.rei_m.hbfavmaterial.presentation.viewmodel.FavoriteBookmarkFragmentViewModel
import javax.inject.Inject

/**
 * お気に入りのブックマークを一覧で表示するFragment.
 */
class FavoriteBookmarkFragment : BaseFragment(),
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
        get() = arguments.getInt(ARG_PAGE_INDEX)

    override val pageTitle: String
        get() = BookmarkPagerAdapter.Page.values()[pageIndex].title(getAppContext(), "")

    @Inject
    lateinit var viewModel: FavoriteBookmarkFragmentViewModel

    @Inject
    lateinit var rxBus: RxBus

    private lateinit var component: BookmarkFavoriteFragmentComponent

    private var binding: FragmentFavoriteBookmarkBinding? = null

    private var footerView: View? = null

    private var disposable: CompositeDisposable? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = FragmentFavoriteBookmarkBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        val adapter = BookmarkListAdapter(context, component, viewModel.bookmarkList)
        binding.listView.adapter = adapter

        footerView = View.inflate(binding.listView.context, R.layout.list_fotter_loading, null)
        binding.listView.addFooterView(footerView)

        this.binding = binding

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
        disposable = CompositeDisposable()
        disposable?.add(rxBus.toObservable().subscribeBus({
            when (it) {
                is ReadAllListItemEvent -> {
                    binding?.listView?.removeFooterView(footerView)
                }
            }
        }))
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
        footerView = null
        binding = null
        super.onDestroyView()
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
