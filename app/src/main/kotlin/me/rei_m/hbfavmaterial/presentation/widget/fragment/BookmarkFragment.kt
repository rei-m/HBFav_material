package me.rei_m.hbfavmaterial.presentation.widget.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import dagger.Binds
import dagger.android.AndroidInjector
import dagger.android.support.DaggerFragment
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.databinding.FragmentBookmarkBinding
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.model.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.BookmarkFragmentViewModel
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.di.BookmarkFragmentViewModelModule
import javax.inject.Inject

class BookmarkFragment : DaggerFragment(), MovableWithAnimation {

    companion object {

        private const val ARG_BOOKMARK = "ARG_BOOKMARK"

        fun newInstance(bookmarkEntity: BookmarkEntity) = BookmarkFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_BOOKMARK, bookmarkEntity)
            }
        }
    }

    override var containerWidth: Float = 0.0f

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var viewModelFactory: BookmarkFragmentViewModel.Factory

    private lateinit var viewModel: BookmarkFragmentViewModel

    private var disposable: CompositeDisposable? = null

    private var listener: OnFragmentInteractionListener? = null

    private val bookmark: BookmarkEntity by lazy {
        requireNotNull(arguments?.getSerializable(ARG_BOOKMARK)) {
            "Arguments is NULL $ARG_BOOKMARK"
        }.let {
            it as BookmarkEntity
        }
    }

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
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(BookmarkFragmentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        container?.let { setContainerWidth(it) }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        disposable = CompositeDisposable()
        disposable?.addAll(viewModel.onClickBodyEvent.subscribe {
            listener?.onShowArticle(it)
        }, viewModel.onClickHeaderEvent.subscribe {
            navigator.navigateToOthersBookmark(it)
        }, viewModel.onClickBookmarkCountEvent.subscribe {
            navigator.navigateToBookmarkedUsers(it)
        })
    }

    override fun onPause() {
        disposable?.dispose()
        disposable = null
        super.onPause()
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val animator = createAnimatorMoveSlide(transit, enter, nextAnim, activity!!)
        return animator ?: super.onCreateAnimation(transit, enter, nextAnim)
    }

    interface OnFragmentInteractionListener {
        fun onShowArticle(url: String)
    }

    @ForFragment
    @dagger.Subcomponent(modules = arrayOf(BookmarkFragmentViewModelModule::class))
    interface Subcomponent : AndroidInjector<BookmarkFragment> {
        @dagger.Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<BookmarkFragment>() {

            abstract fun viewModelModule(module: BookmarkFragmentViewModelModule): Builder

            override fun seedInstance(instance: BookmarkFragment) {
                viewModelModule(BookmarkFragmentViewModelModule(instance.bookmark))
            }
        }
    }

    @dagger.Module(subcomponents = arrayOf(Subcomponent::class))
    abstract inner class Module {
        @Binds
        @IntoMap
        @FragmentKey(BookmarkFragment::class)
        internal abstract fun bind(builder: Subcomponent.Builder): AndroidInjector.Factory<out Fragment>
    }
}
