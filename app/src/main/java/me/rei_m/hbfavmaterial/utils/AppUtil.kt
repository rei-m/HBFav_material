package me.rei_m.hbfavmaterial.utils

import android.content.Context

class AppUtil private constructor() {

    companion object {

        fun getVersionCode(context: Context): Int {
            return context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        }

        fun getVersionName(context: Context): String {
            return context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }
    }
}
