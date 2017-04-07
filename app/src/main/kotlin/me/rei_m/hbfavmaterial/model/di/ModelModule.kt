package me.rei_m.hbfavmaterial.model.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.infra.network.HatenaApiService
import me.rei_m.hbfavmaterial.infra.network.HatenaBookmarkService
import me.rei_m.hbfavmaterial.infra.network.HatenaHotEntryRssService
import me.rei_m.hbfavmaterial.infra.network.HatenaRssService
import me.rei_m.hbfavmaterial.model.*
import javax.inject.Singleton

@Module
class ModelModule {

    @Provides
    @Singleton
    fun provideUserModel(context: Context,
                         hatenaBookmarkService: HatenaBookmarkService): UserModel {
        return UserModel(context.getAppPreferences("UserModel"), hatenaBookmarkService)
    }

    @Provides
    @Singleton
    fun provideBookmarkModel(hatenaApiService: HatenaApiService): BookmarkModel {
        return BookmarkModel(hatenaApiService)
    }

    @Provides
    @Singleton
    fun provideFavoriteBookmarkModel(hatenaRssService: HatenaRssService): FavoriteBookmarkModel {
        return FavoriteBookmarkModel(hatenaRssService)
    }

    @Provides
    @Singleton
    fun provideUserBookmarkModel(hatenaRssService: HatenaRssService): UserBookmarkModel {
        return UserBookmarkModel(hatenaRssService)
    }

    @Provides
    @Singleton
    fun provideHotEntryModel(hatenaRssService: HatenaRssService,
                             hatenaHotEntryRssService: HatenaHotEntryRssService): HotEntryModel {
        return HotEntryModel(hatenaRssService, hatenaHotEntryRssService)
    }

    @Provides
    @Singleton
    fun provideNewEntryModel(hatenaRssService: HatenaRssService): NewEntryModel {
        return NewEntryModel(hatenaRssService)
    }

    /**
     * アプリ内で使用するSharedPreferenceを取得する.
     */
    private fun Context.getAppPreferences(key: String): SharedPreferences {
        return getSharedPreferences(key, Context.MODE_PRIVATE)
    }
}
