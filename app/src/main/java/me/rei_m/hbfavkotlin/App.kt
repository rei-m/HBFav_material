package me.rei_m.hbfavkotlin

import android.app.Application
import me.rei_m.hbfavkotlin.managers.ModelLocator
import me.rei_m.hbfavkotlin.models.*
import me.rei_m.hbfavkotlin.managers.ModelLocator.Companion.Tag as ModelTag

public class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Application起動時に実行される。アプリの初期処理など

        // ModelLocatorにModelの参照を登録
        ModelLocator.register(ModelTag.FAVORITE, BookmarkFavoriteModel());
        ModelLocator.register(ModelTag.OWN, BookmarkOwnModel());
        ModelLocator.register(ModelTag.HOT_ENTRY, HotEntryModel());
        ModelLocator.register(ModelTag.NEW_ENTRY, NewEntryModel());
        ModelLocator.register(ModelTag.USER, UserModel(applicationContext));
    }
}