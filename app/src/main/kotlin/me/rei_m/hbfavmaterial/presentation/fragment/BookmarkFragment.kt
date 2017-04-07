package me.rei_m.hbfavmaterial.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import me.rei_m.hbfavmaterial.databinding.FragmentBookmarkBinding
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.model.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.presentation.fragment.di.BookmarkFragmentComponent
import me.rei_m.hbfavmaterial.presentation.fragment.di.BookmarkFragmentModule
import me.rei_m.hbfavmaterial.viewmodel.fragment.BookmarkFragmentViewModel
import javax.inject.Inject

class BookmarkFragment : BaseFragment(), MovableWithAnimation {

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

    private lateinit var component: BookmarkFragmentComponent

    private val bookmarkEntity: BookmarkEntity by lazy {
        arguments.getSerializable(ARG_BOOKMARK) as BookmarkEntity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        viewModel.bookmark.set(bookmarkEntity)

        container?.let { setContainerWidth(it) }

        return binding.root
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val animator = createAnimatorMoveSlide(transit, enter, nextAnim, activity)
        return animator ?: super.onCreateAnimation(transit, enter, nextAnim)
    }

    @Suppress("UNCHECKED_CAST")
    override fun setupFragmentComponent() {
        component = (activity as HasComponent<Injector>).getComponent()
                .plus(BookmarkFragmentModule())
        component.inject(this)
    }

    interface Injector {
        fun plus(fragmentModule: BookmarkFragmentModule?): BookmarkFragmentComponent
    }
}
