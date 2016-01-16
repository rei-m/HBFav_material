package me.rei_m.hbfavmaterial

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.squareup.leakcanary.LeakCanary
import com.twitter.sdk.android.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import io.fabric.sdk.android.Fabric
import me.rei_m.hbfavmaterial.di.AppLayerModule
import me.rei_m.hbfavmaterial.di.ApplicationComponent
import me.rei_m.hbfavmaterial.di.ApplicationModule
import me.rei_m.hbfavmaterial.di.DaggerApplicationComponent
import me.rei_m.hbfavmaterial.extensions.getAssetToJson
import me.rei_m.hbfavmaterial.managers.ModelLocator
import me.rei_m.hbfavmaterial.managers.ModelLocator.Companion.Tag
import me.rei_m.hbfavmaterial.models.*
import javax.inject.Inject
import javax.inject.Named

class App : Application() {

    @Inject
    lateinit var bookmarkFavoriteModel: BookmarkFavoriteModel

    @field:[Inject Named("bookmarkUserModelForSelf")]
    lateinit var bookmarkUserModel: BookmarkUserModel

    @Inject
    lateinit var hotEntryModel: HotEntryModel

    @Inject
    lateinit var newEntryModel: NewEntryModel

    companion object {
        // platformStatic allow access it from java code
        @JvmStatic lateinit public var graph: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()

        // Application起動時に実行される。アプリの初期処理など

        // Dagger2
        graph = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .appLayerModule(AppLayerModule(this))
                .build()
        graph.inject(this)

        // LeakCanaryの設定
        if (BuildConfig.DEBUG) {
            LeakCanary.install(this);
        }

        // Set up Fabric
        val twitterJson = getAssetToJson("twitter.json")
        val crashlyticsCore = CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        val authConfig = TwitterAuthConfig(twitterJson.getString("consumer_key"), twitterJson.getString("consumer_secret"))
        Fabric.with(this, Crashlytics.Builder().core(crashlyticsCore).build(), Twitter(authConfig))

        // ModelLocatorにModelの参照を登録
        ModelLocator.apply {
            register(Tag.FAVORITE, BookmarkFavoriteModel())
            register(Tag.OWN_BOOKMARK, BookmarkUserModel())
            register(Tag.HOT_ENTRY, HotEntryModel())
            register(Tag.NEW_ENTRY, NewEntryModel())
            register(Tag.USER, UserModel(applicationContext))
            register(Tag.OTHERS_BOOKMARK, BookmarkUserModel())
            register(Tag.USER_REGISTER_BOOKMARK, UserRegisterBookmarkModel())
            register(Tag.HATENA, HatenaModel(applicationContext))
            register(Tag.TWITTER, TwitterModel(applicationContext))
        }
    }

    fun resetBookmarks() {
        bookmarkFavoriteModel.bookmarkList.clear()
        bookmarkUserModel.bookmarkList.clear()
        hotEntryModel.entryList.clear()
        newEntryModel.entryList.clear()
    }
}
