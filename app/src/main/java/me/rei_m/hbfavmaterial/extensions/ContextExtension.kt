package me.rei_m.hbfavmaterial.extensions

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * アプリ内で使用するSharedPreferenceを取得する.
 */
fun Context.getAppPreferences(key: String): SharedPreferences {
    return getSharedPreferences(key, Context.MODE_PRIVATE)
}

/**
 * Asset内のJsonファイルをJSONオブジェクトに変換して取得する.
 */
fun Context.getAssetToJson(fileName: String): JSONObject {
    val inputStream = resources.assets.open(fileName)
    val br = BufferedReader(InputStreamReader(inputStream))
    val json = br.readText()
    br.close()
    return JSONObject(json)
}
