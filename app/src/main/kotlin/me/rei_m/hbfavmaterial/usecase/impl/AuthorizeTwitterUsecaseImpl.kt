package me.rei_m.hbfavmaterial.usecase.impl

import android.app.Activity
import android.content.Intent
import me.rei_m.hbfavmaterial.domain.service.TwitterService
import me.rei_m.hbfavmaterial.usecase.AuthorizeTwitterUsecase

class AuthorizeTwitterUsecaseImpl(private val twitterService: TwitterService) : AuthorizeTwitterUsecase {

    override fun authorize(activity: Activity) {
        twitterService.authorize(activity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        twitterService.onActivityResult(requestCode, resultCode, data)
    }
}
