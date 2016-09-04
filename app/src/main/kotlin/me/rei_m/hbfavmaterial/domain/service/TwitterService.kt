package me.rei_m.hbfavmaterial.domain.service

import android.app.Activity
import android.content.Intent

interface TwitterService {

    fun authorize(activity: Activity)

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    fun postTweet(text: String)
}
