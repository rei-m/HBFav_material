package me.rei_m.hbfavmaterial.di

import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.service.BookmarkService
import me.rei_m.hbfavmaterial.service.impl.BookmarkServiceImpl

@Module
class ActivityModule(val activity: AppCompatActivity) {

    @Provides
    fun provideBookmarkService(): BookmarkService {
        return BookmarkServiceImpl()
    }
}
