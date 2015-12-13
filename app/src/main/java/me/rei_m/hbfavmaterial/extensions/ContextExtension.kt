package me.rei_m.hbfavmaterial.extensions

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

fun Context.getAppPreferences(key: String): SharedPreferences {
    return getSharedPreferences(key, Context.MODE_PRIVATE)
}

fun Context.getAssetToJson(fileName: String): JSONObject {
    val hatenaInputStream = resources.assets.open(fileName)
    val br = BufferedReader(InputStreamReader(hatenaInputStream))
    val json = br.readText()
    br.close()

    return JSONObject(json)
}