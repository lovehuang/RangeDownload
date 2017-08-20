package downloader.wxy.com.rangedownloader

import org.json.JSONObject

/**
 * Created by wangxiaoyan on 2017/8/16.
 */
// TODO 此处应该用数据库,data数据类型理论上可以跟JSON互转
data class DownLoadEntity(val name: String,
                          var totalLength: Int = 0,
                          var currentLength: Int = 0,
                          var filePath: String = "",
                          val url: String,
                          var status: Int = 0) {


    companion object {
        // 空闲未开始
        val STATUS_IDLE = 0
        // 正在下载
        val STATUS_DOWNLOADLING = 1
        // 下载成功
        val STATUS_SUCCEED = 2
        // 下载失败
        val STATUS_FAILED = 3
        // 删除该数据
        val STATUS_DELETE = 4

        fun getEntity(json: String): DownLoadEntity {
            val json = JSONObject(json)
            return DownLoadEntity(json.getString("name"),
                    json.getInt("totalLength"),
                    json.getInt("currentLength"),
                    json.getString("filePath"),
                    json.getString("url"),
                    json.getInt("status"))
        }
    }

    override fun toString(): String {
        return JSONObject().put("name", name)
                .put("totalLength", totalLength)
                .put("currentLength", currentLength)
                .put("filePath", filePath)
                .put("url", url)
                .put("status", status).toString()
    }
}
