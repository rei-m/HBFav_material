package me.rei_m.hbfavmaterial.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.views.widgets.bookmark.BookmarkContentsLayout
import me.rei_m.hbfavmaterial.views.widgets.bookmark.BookmarkCountTextView
import me.rei_m.hbfavmaterial.views.widgets.bookmark.BookmarkHeaderLayout

class BookmarkFragment() : BaseFragment(), MovableWithAnimation {

    companion object {

        private const val ARG_BOOKMARK = "ARG_BOOKMARK"

        fun newInstance(bookmarkEntity: BookmarkEntity): BookmarkFragment {
            return BookmarkFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_BOOKMARK, bookmarkEntity)
                }
            }
        }
    }

    private var listener: OnFragmentInteractionListener? = null

    private val bookmarkEntity: BookmarkEntity by lazy {
        arguments.getSerializable(ARG_BOOKMARK) as BookmarkEntity
    }

    override var containerWidth: Float = 0.0f

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_bookmark, container, false)

        val bookmarkHeaderLayout = view.findViewById(R.id.fragment_bookmark_layout_header) as BookmarkHeaderLayout

        val bookmarkContents = view.findViewById(R.id.layout_bookmark_contents) as BookmarkContentsLayout

        val bookmarkCountTextView = view.findViewById(R.id.fragment_bookmark_text_bookmark_count) as BookmarkCountTextView

        with(bookmarkEntity) {
            bookmarkHeaderLayout.bindView(this)
            bookmarkHeaderLayout.setOnClickListener {
                listener?.onClickBookmarkUser(this)
            }

            bookmarkContents.bindView(this)
            bookmarkContents.setOnClickListener {
                listener?.onClickBookmark(this)
            }

            bookmarkCountTextView.bindView(this)
            bookmarkCountTextView.setOnClickListener {
                listener?.onClickBookmarkCount(this)
            }
        }

        setContainerWidth(container!!)

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        listener = null
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val animator = createAnimatorMoveSlide(transit, enter, nextAnim, activity)
        return animator ?: super.onCreateAnimation(transit, enter, nextAnim)
    }

    interface OnFragmentInteractionListener {

        fun onClickBookmarkUser(bookmarkEntity: BookmarkEntity)

        fun onClickBookmark(bookmarkEntity: BookmarkEntity)

        fun onClickBookmarkCount(bookmarkEntity: BookmarkEntity)
    }
}
