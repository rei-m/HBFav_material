package me.rei_m.hbfavmaterial

import android.content.Context
import android.support.multidex.MultiDex
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.google.firebase.analytics.FirebaseAnalytics
import com.squareup.leakcanary.LeakCanary
import com.twitter.sdk.android.core.*
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import io.fabric.sdk.android.Fabric
import me.rei_m.hbfavmaterial.di.ApplicationModule
import me.rei_m.hbfavmaterial.infra.di.InfraLayerModule
import me.rei_m.hbfavmaterial.model.di.ModelModule
import me.rei_m.hbfavmaterial.presentation.activity.*
import javax.inject.Singleton

open class App : DaggerApplication() {

    private lateinit var analytics: FirebaseAnalytics

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this);
    }

    override fun onCreate() {
        super.onCreate()

        // Application起動時に実行される。アプリの初期処理など

        // LeakCanaryの設定
        if (BuildConfig.DEBUG) {
            LeakCanary.install(this)
        }

        // Set up Fabric
        val authConfig = TwitterAuthConfig(getString(R.string.api_key_twitter_consumer_key),
                getString(R.string.api_key_twitter_consumer_secret))

        val config = TwitterConfig.Builder(this)
                .logger(DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(authConfig)
                .debug(true)
                .build()

        Twitter.initialize(config)

        val crashlyticsCore = CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()
        Fabric.with(this, Crashlytics.Builder().core(crashlyticsCore).build())

        // Set up FireBase Analytics
        analytics = FirebaseAnalytics.getInstance(this)
    }

    override fun applicationInjector(): AndroidInjector<App> {
        return DaggerApp_Component.builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }

    @Singleton
    @dagger.Component(modules = arrayOf(
            AndroidSupportInjectionModule::class,
            ApplicationModule::class,
            InfraLayerModule::class,
            ModelModule::class,
            BookmarkActivity.Module::class,
            BookmarkedUsersActivity.Module::class,
            ExplainAppActivity.Module::class,
            MainActivity.Module::class,
            OAuthActivity.Module::class,
            OthersBookmarkActivity.Module::class,
            SettingActivity.Module::class,
            SplashActivity.Module::class))
    internal interface Component : AndroidInjector<App>
}
