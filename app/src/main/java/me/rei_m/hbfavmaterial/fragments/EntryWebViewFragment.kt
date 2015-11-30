package me.rei_m.hbfavmaterial.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import me.rei_m.hbfavmaterial.R

public class EntryWebViewFragment : Fragment() {

    private var mEntryUrl: String? = null

    companion object {

        private val ARG_ENTRY_URL = "ARG_ENTRY_URL"

        public fun newInstance(entryUrl: String): EntryWebViewFragment {
            val fragment = EntryWebViewFragment()
            val args = Bundle()
            args.putString(ARG_ENTRY_URL, entryUrl)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEntryUrl = arguments.getString(ARG_ENTRY_URL)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_bookmark_webview, container, false)

        val webView = view.findViewById(R.id.view_bookmark_web) as WebView
        webView.setWebChromeClient(WebChromeClient())
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(mEntryUrl)

        return view
    }

    override fun onDetach() {
        super.onDetach()
        mEntryUrl = null
    }
}
