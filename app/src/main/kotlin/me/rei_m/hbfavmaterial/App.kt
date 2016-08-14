package me.rei_m.hbfavmaterial

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.google.firebase.analytics.FirebaseAnalytics
import com.squareup.leakcanary.LeakCanary
import com.twitter.sdk.android.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import io.fabric.sdk.android.Fabric
import me.rei_m.hbfavmaterial.di.ApplicationComponent
import me.rei_m.hbfavmaterial.di.ApplicationModule
import me.rei_m.hbfavmaterial.di.DaggerApplicationComponent
import me.rei_m.hbfavmaterial.di.InfraLayerModule

class App : Application() {

    lateinit var component: ApplicationComponent

    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()

        // Application起動時に実行される。アプリの初期処理など

        // Dagger2
        component = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .infraLayerModule(InfraLayerModule())
                .build()
        component.inject(this)

        // LeakCanaryの設定
        if (BuildConfig.DEBUG) {
//            LeakCanary.install(this);
        }

        // Set up Fabric
        val crashlyticsCore = CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        val authConfig = TwitterAuthConfig(getString(R.string.api_key_twitter_consumer_key),
                getString(R.string.api_key_twitter_consumer_secret))
        Fabric.with(this, Crashlytics.Builder().core(crashlyticsCore).build(), Twitter(authConfig))

        // Set up FireBase Analytics
        analytics = FirebaseAnalytics.getInstance(this)
    }
}
