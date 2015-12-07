package me.rei_m.hbfavmaterial.utils

import android.content.Context

class AppUtil private constructor() {

    companion object {

        public fun getVersionCode(context: Context): Int {
            val pm = context.packageManager
            return pm.getPackageInfo(context.packageName, 0).versionCode
        }

        public fun getVersionName(context: Context): String {
            val pm = context.packageManager
            return pm.getPackageInfo(context.packageName, 0).versionName
        }

    }
}