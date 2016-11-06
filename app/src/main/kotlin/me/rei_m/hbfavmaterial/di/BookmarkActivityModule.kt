package me.rei_m.hbfavmaterial.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.repository.HatenaTokenRepository
import me.rei_m.hbfavmaterial.domain.service.HatenaService
import me.rei_m.hbfavmaterial.usecase.GetBookmarkEditUsecase
import me.rei_m.hbfavmaterial.usecase.GetHatenaTokenUsecase
import me.rei_m.hbfavmaterial.usecase.impl.GetBookmarkEditUsecaseImpl
import me.rei_m.hbfavmaterial.usecase.impl.GetHatenaTokenUsecaseImpl


@Module
class BookmarkActivityModule(private val context: Context)
