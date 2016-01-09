package me.rei_m.hbfavmaterial

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.squareup.leakcanary.LeakCanary
import com.twitter.sdk.android.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import io.fabric.sdk.android.Fabric
import me.rei_m.hbfavmaterial.extensions.getAssetToJson
import me.rei_m.hbfavmaterial.managers.ModelLocator
import me.rei_m.hbfavmaterial.managers.ModelLocator.Companion.Tag
import me.rei_m.hbfavmaterial.models.*

public class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Application起動時に実行される。アプリの初期処理など

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

    public fun resetBookmarks() {

        val favoriteModel = ModelLocator.get(Tag.FAVORITE) as BookmarkFavoriteModel
        val ownModel = ModelLocator.get(Tag.OWN_BOOKMARK) as BookmarkUserModel
        val hotEntryModel = ModelLocator.get(Tag.HOT_ENTRY) as HotEntryModel
        val newEntryModel = ModelLocator.get(Tag.NEW_ENTRY) as NewEntryModel

        favoriteModel.bookmarkList.clear()
        ownModel.bookmarkList.clear()
        hotEntryModel.entryList.clear()
        newEntryModel.entryList.clear()
    }
}
