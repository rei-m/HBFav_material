package me.rei_m.hbfavmaterial.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.databinding.FragmentBookmarkBinding
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.model.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.viewmodel.fragment.BookmarkFragmentViewModel
import me.rei_m.hbfavmaterial.viewmodel.fragment.di.BookmarkFragmentViewModelModule
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
    lateinit var viewModel: BookmarkFragmentViewModel

    private var disposable: CompositeDisposable? = null

    private var listener: OnFragmentInteractionListener? = null

    private val bookmarkEntity: BookmarkEntity by lazy {
        arguments?.getSerializable(ARG_BOOKMARK) as BookmarkEntity
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onCreate(bookmarkEntity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        container?.let { setContainerWidth(it) }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
        disposable = CompositeDisposable()
        disposable?.add(viewModel.showArticleEvent.subscribe {
            listener?.onShowArticle(it)
        })
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

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val animator = createAnimatorMoveSlide(transit, enter, nextAnim, activity!!)
        return animator ?: super.onCreateAnimation(transit, enter, nextAnim)
    }

    interface OnFragmentInteractionListener {
        fun onShowArticle(url: String)
    }

    @dagger.Module
    abstract inner class Module {
        @ForFragment
        @ContributesAndroidInjector(modules = arrayOf(BookmarkFragmentViewModelModule::class))
        internal abstract fun contributeInjector(): BookmarkFragment
    }
}
