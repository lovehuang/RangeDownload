package downloader.wxy.com.rangedownloader

import android.content.Context
import java.io.InputStream
import java.io.RandomAccessFile
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by wangxiaoyan on 2017/8/20.
 */

class DownLoadThread internal constructor(private val mContext: Context, private val mEntity: DownLoadEntity, private val mListener: DownloadListener) : Thread() {

    override fun run() {
        super.run()
        var urlConnection: HttpURLConnection
        var randomFile: RandomAccessFile
        var inputStream: InputStream
        try {
            val url = URL("http://www.baidu.com")
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connectTimeout = 3000
            urlConnection.requestMethod = "GET"
            urlConnection.requestMethod = "HEAD"
            urlConnection.allowUserInteraction = true;
            urlConnection.connect();
            //设置下载位置
            val start = mEntity.currentLength
            if (mEntity.totalLength != 0) { //第一次没必要设置断点续传
                urlConnection.setRequestProperty("Range", "bytes=" + start + "-" + mEntity.totalLength)
            }

            var length = -1
            if (urlConnection.responseCode / 100 == 2) {
                //获得文件长度
                length = urlConnection.contentLength
            }
            if (length <= 0) {
                return
            }


        } catch (e: Exception) {
            e.printStackTrace()
        } finally {  //流的回收逻辑略
        }
    }
}
