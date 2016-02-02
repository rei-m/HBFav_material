package me.rei_m.hbfavmaterial.fragments

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import me.rei_m.hbfavmaterial.databinding.FragmentBookmarkWebviewBinding

/**
 * 記事のコンテンツをWebViewに表示するFragment.
 */
class EntryWebViewFragment : Fragment() {

    lateinit private var mEntryUrl: String

    companion object {

        val TAG = EntryWebViewFragment::class.java.simpleName

        private val ARG_ENTRY_URL = "ARG_ENTRY_URL"

        fun newInstance(entryUrl: String): EntryWebViewFragment {
            return EntryWebViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ENTRY_URL, entryUrl)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEntryUrl = arguments.getString(ARG_ENTRY_URL)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = FragmentBookmarkWebviewBinding.inflate(inflater, container, false)

        binding.fragmentBookmarkWebviewView.apply {
            settings.apply {
                javaScriptEnabled = true
                builtInZoomControls = true
                loadWithOverviewMode = true
                useWideViewPort = true
                setGeolocationEnabled(true)
                setSupportMultipleWindows(true)
            }
            setWebChromeClient(WebChromeClient())
            setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            })
            loadUrl(mEntryUrl)
        }

        return binding.root
    }

    /**
     * WebView内のコンテンツがヒストリバック可能な場合、ヒストリバックして表示する.
     *
     * @return ヒストリバックしない場合 true, した場合 false
     */
    fun backHistory(): Boolean {

        val binding = DataBindingUtil.getBinding<FragmentBookmarkWebviewBinding>(view)

        binding.fragmentBookmarkWebviewView ?: return true
        if (binding.fragmentBookmarkWebviewView.canGoBack()) {
            binding.fragmentBookmarkWebviewView.goBack()
            return false
        } else {
            return true
        }
    }
}
