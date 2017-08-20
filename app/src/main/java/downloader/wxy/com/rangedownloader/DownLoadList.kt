package downloader.wxy.com.rangedownloader

import android.content.Context
import org.json.JSONArray

/**
 * Created by wangxiaoyan on 2017/8/20.
 */


// TODO 此处应该用数据库


private val PREFERENCE_NAME = "DOWNLOAD_LIST"
val SAVE_NAME = "download_list_name"

fun saveDownLoadList(context: Context, entitys: MutableList<DownLoadEntity>) {
    val pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    val jsonArray = JSONArray()
    entitys.forEach { jsonArray.put(it.toString()) }
    pref.edit().putString(SAVE_NAME, jsonArray.toString()).apply()
}

fun getDownLoadList(context: Context): MutableList<DownLoadEntity> {
    val pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    val jsonArrayString = pref.getString(SAVE_NAME, "")
    var list = mutableListOf<DownLoadEntity>()
    if (!jsonArrayString.isEmpty()) {
        val jsonArray = JSONArray(jsonArrayString)
        for (i in 0..(jsonArray.length() - 1)) {
            list.add(DownLoadEntity.getEntity(jsonArray.getString(i)))
        }
    }

    return list
}