package me.rei_m.hbfavkotlin

import android.app.Application
import me.rei_m.hbfavkotlin.managers.ModelLocator
import me.rei_m.hbfavkotlin.models.BookmarkFavoriteModel
import me.rei_m.hbfavkotlin.models.BookmarkOwnModel
import me.rei_m.hbfavkotlin.models.HotEntryModel

public class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Application起動時に実行される。アプリの初期処理など

        // ModelLocatorにModelの参照を登録
        ModelLocator.register(ModelLocator.Companion.Tag.FAVORITE, BookmarkFavoriteModel());
        ModelLocator.register(ModelLocator.Companion.Tag.OWN, BookmarkOwnModel());
        ModelLocator.register(ModelLocator.Companion.Tag.HOT_ENTRY, HotEntryModel());
    }
}