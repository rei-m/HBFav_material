package me.rei_m.hbfavmaterial.usecase

import android.app.Activity
import android.content.Intent

interface AuthorizeTwitterUsecase {

    fun authorize(activity: Activity)

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}
