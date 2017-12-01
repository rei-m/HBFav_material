/*
 * Copyright (c) 2017. Rei Matsushita
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

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
import javax.inject.Named
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
    @Named("othersUserBookmarkModel")
    fun provideOthersUserBookmarkModel(hatenaRssService: HatenaRssService): UserBookmarkModel {
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
