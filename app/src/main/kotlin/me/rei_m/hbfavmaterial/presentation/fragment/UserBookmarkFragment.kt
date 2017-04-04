package me.rei_m.hbfavmaterial.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.constant.ReadAfterFilter
import me.rei_m.hbfavmaterial.databinding.FragmentUserBookmarkBinding
import me.rei_m.hbfavmaterial.di.BookmarkUserFragmentComponent
import me.rei_m.hbfavmaterial.di.BookmarkUserFragmentModule
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.extension.getAppContext
import me.rei_m.hbfavmaterial.extension.subscribeBus
import me.rei_m.hbfavmaterial.presentation.event.ReadAllListItemEvent
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.view.adapter.BookmarkListAdapter
import me.rei_m.hbfavmaterial.presentation.view.adapter.BookmarkPagerAdapter
import me.rei_m.hbfavmaterial.presentation.viewmodel.UserBookmarkFragmentViewModel
import javax.inject.Inject

/**
 * 特定のユーザーのブックマークを一覧で表示するFragment.
 */
class UserBookmarkFragment : BaseFragment(),
        MainPageFragment {

    companion object {

        private const val ARG_PAGE_INDEX = "ARG_PAGE_INDEX"

        private const val ARG_USER_ID = "ARG_USER_ID"

        private const val ARG_OWNER_FLAG = "ARG_OWNER_FLAG"

        private const val KEY_FILTER_TYPE = "KEY_FILTER_TYPE"

        /**
         * 自分のブックマークを表示する
         *
         * @return Fragment
         */
        fun newInstance(pageIndex: Int) = UserBookmarkFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARG_OWNER_FLAG, true)
                putInt(ARG_PAGE_INDEX, pageIndex)
            }
        }

        /**
         * 他人のブックマークを表示する
         *
         * @userId: 表示対象のユーザーのID.
         * @return Fragment
         */
        fun newInstance(userId: String) = UserBookmarkFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_USER_ID, userId)
                putBoolean(ARG_OWNER_FLAG, false)
                putInt(ARG_PAGE_INDEX, 0)
            }
        }
    }

    override val pageIndex: Int
        get() = arguments.getInt(ARG_PAGE_INDEX)

    override val pageTitle: String
        get() = BookmarkPagerAdapter.Page.values()[pageIndex].title(getAppContext(), viewModel.readAfterFilter.title(getAppContext()))

    @Inject
    lateinit var viewModel: UserBookmarkFragmentViewModel

    @Inject
    lateinit var rxBus: RxBus

    private lateinit var component: BookmarkUserFragmentComponent

    private var binding: FragmentUserBookmarkBinding? = null

    private var footerView: View? = null

    private var disposable: CompositeDisposable? = null

    private var listener: OnFragmentInteractionListener? = null

    private val isOwner: Boolean by lazy {
        arguments.getBoolean(ARG_OWNER_FLAG)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val userId = arguments.getString(ARG_USER_ID) ?: ""

        require(isOwner || (!isOwner && userId.isNotEmpty())) {
            "UserId is empty !!"
        }

        val readAfterFilter = if (savedInstanceState != null) {
            savedInstanceState.getSerializable(KEY_FILTER_TYPE) as ReadAfterFilter
        } else {
            ReadAfterFilter.ALL
        }

        viewModel.isOwner = isOwner
        viewModel.bookmarkUserId = userId
        viewModel.readAfterFilter = readAfterFilter
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = FragmentUserBookmarkBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        val adapter = BookmarkListAdapter(context, component, viewModel.bookmarkList)
        binding.listView.adapter = adapter

        // ここイマイチすぎる。。。RecyclerViewに書き換えつつ綺麗にしたい.
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

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (isOwner) {
            inflater?.inflate(R.menu.fragment_bookmark_user, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        item ?: return false

        val filter = ReadAfterFilter.forMenuId(item.itemId)

        viewModel.onOptionItemSelected(filter)

        listener?.onChangeFilter(pageTitle)

        return true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable(KEY_FILTER_TYPE, viewModel.readAfterFilter)
    }

    @Suppress("UNCHECKED_CAST")
    override fun setupFragmentComponent() {
        component = (activity as HasComponent<Injector>).getComponent()
                .plus(BookmarkUserFragmentModule(this))
        component.inject(this)
    }

    interface OnFragmentInteractionListener {
        fun onChangeFilter(newPageTitle: String)
    }

    interface Injector {
        fun plus(fragmentModule: BookmarkUserFragmentModule?): BookmarkUserFragmentComponent
    }
}
