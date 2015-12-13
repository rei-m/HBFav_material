package me.rei_m.hbfavmaterial

import android.app.Application
import me.rei_m.hbfavmaterial.managers.ModelLocator
import me.rei_m.hbfavmaterial.managers.ModelLocator.Companion.Tag
import me.rei_m.hbfavmaterial.models.*

public class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Application起動時に実行される。アプリの初期処理など

        // ModelLocatorにModelの参照を登録
        ModelLocator.register(Tag.FAVORITE, BookmarkFavoriteModel())
        ModelLocator.register(Tag.OWN_BOOKMARK, BookmarkUserModel())
        ModelLocator.register(Tag.HOT_ENTRY, HotEntryModel())
        ModelLocator.register(Tag.NEW_ENTRY, NewEntryModel())
        ModelLocator.register(Tag.USER, UserModel(applicationContext))
        ModelLocator.register(Tag.OTHERS_BOOKMARK, BookmarkUserModel())
        ModelLocator.register(Tag.USER_REGISTER_BOOKMARK, UserRegisterBookmarkModel())
        ModelLocator.register(Tag.HATENA, HatenaModel(applicationContext))
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