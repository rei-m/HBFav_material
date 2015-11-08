package me.rei_m.hbfavkotlin.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation

import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.models.Bookmark
import me.rei_m.hbfavkotlin.views.widgets.bookmark.ArticleLayout
import me.rei_m.hbfavkotlin.views.widgets.bookmark.BookmarkHeaderLayout
import me.rei_m.hbfavkotlin.views.widgets.bookmark.BookmarkLayout

public class BookmarkFragment private constructor() : Fragment(), FragmentAnimationI {

    private var mListener: OnFragmentInteractionListener? = null

    private var mBookmark: Bookmark? = null

    override var mContainerWidth: Float = 0.0f

    companion object {

        private val ARG_BOOKMARK = "ARG_BOOKMARK"

        public fun newInstance(bookmark: Bookmark): BookmarkFragment {
            val fragment = BookmarkFragment()
            val args = Bundle()
            args.putSerializable(ARG_BOOKMARK, bookmark)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBookmark = arguments.getSerializable(ARG_BOOKMARK) as Bookmark
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_bookmark, container, false)

        (view.findViewById(R.id.layout_bookmark_header) as BookmarkHeaderLayout).bindView(mBookmark!!)

        val bookmarkLayout = view.findViewById(R.id.layout_bookmark) as BookmarkLayout
        bookmarkLayout.bindView(mBookmark!!)
        bookmarkLayout.setOnClickListener { v ->
            mListener?.onClickBookmark(mBookmark!!)
        }

        (view.findViewById(R.id.layout_article) as ArticleLayout).bindView(mBookmark!!)

        setContainer(container!!)

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mListener = activity as OnFragmentInteractionListener;
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val animator = createAnimatorMoveSlide(transit, enter, nextAnim, activity)
        return animator ?: super.onCreateAnimation(transit, enter, nextAnim)
    }

    interface OnFragmentInteractionListener {
        fun onClickBookmark(bookmark: Bookmark)
    }
}
