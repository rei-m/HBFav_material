package me.rei_m.hbfavkotlin.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView

import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.models.Bookmark

public class BookmarkWebViewFragment private constructor() : Fragment() {

    private var mBookmark: Bookmark? = null

    companion object {

        private val ARG_BOOKMARK = "ARG_BOOKMARK"

        public fun newInstance(bookmark: Bookmark): BookmarkWebViewFragment {
            val fragment = BookmarkWebViewFragment()
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

        val view = inflater.inflate(R.layout.fragment_bookmark_webview, container, false)

        val webView = view.findViewById(R.id.view_bookmark_web) as WebView
        webView.setWebChromeClient(WebChromeClient())
        webView.loadUrl(mBookmark!!.link)

        return view
    }

    override fun onDetach() {
        super.onDetach()
        mBookmark = null
    }
}
