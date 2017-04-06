package me.rei_m.hbfavmaterial.application

import android.app.Activity
import android.content.Intent
import io.reactivex.Observable

interface TwitterService {

    val confirmAuthorisedEvent: Observable<Boolean>

    fun confirmAuthorised()

    fun authorize(activity: Activity)

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    fun postTweet(articleUrl: String, articleTitle: String, comment: String)
}
