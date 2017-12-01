/*
 * Copyright (c) 2017. Rei Matsushita
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package me.rei_m.hbfavmaterial.presentation.widget.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import me.rei_m.hbfavmaterial.R

/**
 * 記事のコンテンツをWebViewに表示するFragment.
 */
class EntryWebViewFragment : Fragment() {

    companion object {

        val TAG: String = EntryWebViewFragment::class.java.simpleName

        private const val ARG_ENTRY_URL = "ARG_ENTRY_URL"

        fun newInstance(entryUrl: String): EntryWebViewFragment {
            return EntryWebViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ENTRY_URL, entryUrl)
                }
            }
        }
    }

    private val entryUrl: String by lazy {
        arguments!!.getString(ARG_ENTRY_URL)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_bookmark_webview, container, false)

        with(view.findViewById<WebView>(R.id.fragment_bookmark_webview_view)) {
            with(settings) {
                javaScriptEnabled = true
                builtInZoomControls = true
                loadWithOverviewMode = true
                useWideViewPort = true
                setGeolocationEnabled(true)
                setSupportMultipleWindows(true)
            }
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean =
                        super.shouldOverrideUrlLoading(view, request)
            }
            loadUrl(entryUrl)
        }

        return view
    }

    /**
     * WebView内のコンテンツがヒストリバック可能な場合、ヒストリバックして表示する.
     *
     * @return ヒストリバックしない場合 true, した場合 false
     */
    fun backHistory(): Boolean {

        val view = view ?: return true

        with(view.findViewById<WebView>(R.id.fragment_bookmark_webview_view)) {
            if (canGoBack()) {
                goBack()
                return false
            } else {
                return true
            }
        }
    }
}
