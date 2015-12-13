package me.rei_m.hbfavmaterial.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.events.HatenaOAuthAccessTokenLoadedEvent
import me.rei_m.hbfavmaterial.events.HatenaOAuthRequestTokenLoadedEvent
import me.rei_m.hbfavmaterial.events.LoadedEventResult
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.managers.ModelLocator
import me.rei_m.hbfavmaterial.models.HatenaModel
import me.rei_m.hbfavmaterial.network.HatenaOAuthApi

public class OAuthActivity : BaseActivity() {

    private var mWebView: WebView? = null;

    companion object {

        public fun createIntent(context: Context): Intent {
            return Intent(context, OAuthActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mWebView = WebView(this)
        mWebView!!.clearCache(true)
        mWebView!!.settings.javaScriptEnabled = true
        mWebView!!.setWebChromeClient(WebChromeClient())
        mWebView!!.setWebViewClient(object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {

                if (url?.startsWith(HatenaOAuthApi.CALLBACK)!!) {
                    mWebView!!.stopLoading()
                    mWebView!!.hide()
                    val uri = Uri.parse(url)
                    val hatenaModel = ModelLocator.get(ModelLocator.Companion.Tag.HATENA) as HatenaModel
                    hatenaModel.fetchAccessToken(applicationContext, uri.getQueryParameter("oauth_verifier"))
                } else {
                    super.onPageStarted(view, url, favicon)
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return super.shouldOverrideUrlLoading(view, url);
            }
        })

        val content = findViewById(R.id.content) as FrameLayout
        content.addView(mWebView)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.hide()
    }

    override fun onResume() {
        super.onResume()
        val hatenaModel = ModelLocator.get(ModelLocator.Companion.Tag.HATENA) as HatenaModel
        hatenaModel.fetchRequestToken()
    }

    @Subscribe
    public fun onHatenaOAuthRequestTokenLoaded(event: HatenaOAuthRequestTokenLoadedEvent) {
        when (event.result) {
            LoadedEventResult.COMPLETE -> {
                mWebView?.loadUrl(event.authUrl)
            }
            else -> {
                showSnackbarNetworkError(findViewById(R.id.activity_layout))
            }
        }
    }

    @Subscribe
    public fun onHatenaOAuthAccessTokenLoaded(event: HatenaOAuthAccessTokenLoadedEvent) {
        when (event.result) {
            LoadedEventResult.COMPLETE -> {

            }
            else -> {
                showSnackbarNetworkError(findViewById(R.id.activity_layout))
            }
        }
    }
}
