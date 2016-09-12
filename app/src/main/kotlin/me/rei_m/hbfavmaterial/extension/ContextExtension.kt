package me.rei_m.hbfavmaterial.extension

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

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

/**
 * 外部URLを開くIntentを起動する.
 */
fun Context.openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    startActivity(intent)
}
