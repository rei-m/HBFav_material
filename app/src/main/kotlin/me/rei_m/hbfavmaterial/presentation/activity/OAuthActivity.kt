package me.rei_m.hbfavmaterial.presentation.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.extension.hide
import me.rei_m.hbfavmaterial.extension.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.infra.network.HatenaOAuthManager
import me.rei_m.hbfavmaterial.presentation.activity.di.ActivityModule
import me.rei_m.hbfavmaterial.presentation.activity.di.OAuthActivityModule
import javax.inject.Inject

class OAuthActivity : BaseActivity() {

    companion object {

        const val ARG_AUTHORIZE_STATUS = "ARG_AUTHORIZE_STATUS"
        const val ARG_IS_AUTHORIZE_DONE = "ARG_IS_AUTHORIZE_DONE"

        fun createIntent(context: Context): Intent = Intent(context, OAuthActivity::class.java)
    }

    @Inject
    lateinit var hatenaService: HatenaService

    private var disposable: CompositeDisposable? = null

    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity)
        val toolbar = findViewById(R.id.activity_toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val webView = WebView(this).apply {
            clearCache(true)
            settings.javaScriptEnabled = true
            setWebChromeClient(WebChromeClient())
            setWebViewClient(object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    if (url?.startsWith(HatenaOAuthManager.CALLBACK) ?: false) {
                        stopLoading()
                        hide()
                        val oauthVerifier = Uri.parse(url).getQueryParameter("oauth_verifier")
                        oauthVerifier ?: finish()
                        hatenaService.registerAccessToken(oauthVerifier)
                    } else if (url?.startsWith(HatenaOAuthManager.AUTHORIZATION_DENY_URL) ?: false) {
                        stopLoading()
                        hatenaService.deleteAccessToken()
                    } else {
                        super.onPageStarted(view, url, favicon)
                    }
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    return super.shouldOverrideUrlLoading(view, url)
                }
            })
        }

        with(findViewById(R.id.content) as FrameLayout) {
            addView(webView)
        }
        findViewById(R.id.fab)?.hide()
        this.webView = webView
    }

    override fun onStart() {
        super.onStart()
        disposable = CompositeDisposable()
        disposable?.addAll(hatenaService.completeFetchRequestTokenEvent.subscribe {
            webView?.loadUrl(it)
        }, hatenaService.completeRegisterAccessTokenEvent.subscribe {
            setAuthorizeResult(true, true)
            finish()
        }, hatenaService.completeDeleteAccessTokenEvent.subscribe {
            setAuthorizeResult(false, true)
            finish()
        }, hatenaService.failAuthorizeHatenaEvent.subscribe {
            setAuthorizeResult(false, false)
            finish()
        }, hatenaService.error.subscribe {
            showSnackbarNetworkError()
        })
    }

    override fun onResume() {
        super.onResume()
        hatenaService.fetchRequestToken()
    }

    override fun onStop() {
        super.onStop()
        disposable?.dispose()
        disposable = null
    }

    override fun onDestroy() {
        super.onDestroy()
        webView = null
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId

        when (id) {
            android.R.id.home ->
                finish()
            else ->
                return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun setUpActivityComponent() {
        val component = (application as App).component
                .plus(OAuthActivityModule(), ActivityModule(this))
        component.inject(this)
    }

    private fun setAuthorizeResult(isAuthorize: Boolean, isDone: Boolean) {
        val intent = Intent().apply {
            putExtras(Bundle().apply {
                putBoolean(ARG_AUTHORIZE_STATUS, isAuthorize)
                putBoolean(ARG_IS_AUTHORIZE_DONE, isDone)
            })
        }
        // TODO: 認証してなかったらキャンセルにする.
        setResult(RESULT_OK, intent)
    }
}
