package me.rei_m.hbfavmaterial.presentation.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import dagger.Binds
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import dagger.multibindings.IntoMap
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.di.ForActivity
import me.rei_m.hbfavmaterial.extension.hide
import me.rei_m.hbfavmaterial.extension.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.infra.network.HatenaOAuthManager
import me.rei_m.hbfavmaterial.presentation.activity.di.ActivityModule
import javax.inject.Inject

class OAuthActivity : DaggerAppCompatActivity() {

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
        val toolbar = findViewById<Toolbar>(R.id.activity_toolbar) as Toolbar
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

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    return super.shouldOverrideUrlLoading(view, request)
                }
            })
        }

        with(findViewById<FrameLayout>(R.id.content)) {
            addView(webView)
        }
        findViewById<FloatingActionButton>(R.id.fab)?.hide()
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
        }, hatenaService.unauthorizedEvent.subscribe {
            setAuthorizeResult(false, false)
            finish()
        }, hatenaService.raisedErrorEvent.subscribe {
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

    @ForActivity
    @dagger.Subcomponent(modules = arrayOf(ActivityModule::class))
    interface Subcomponent : AndroidInjector<OAuthActivity> {
        @dagger.Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<OAuthActivity>() {

            abstract fun activityModule(module: ActivityModule): Builder

            override fun seedInstance(instance: OAuthActivity) {
                activityModule(ActivityModule(instance))
            }
        }
    }

    @dagger.Module(subcomponents = arrayOf(Subcomponent::class))
    abstract inner class Module {
        @Binds
        @IntoMap
        @ActivityKey(OAuthActivity::class)
        internal abstract fun bind(builder: Subcomponent.Builder): AndroidInjector.Factory<out Activity>
    }
}
