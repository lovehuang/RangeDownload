package downloader.wxy.com.rangedownloader

/**
 * Created by wangxiaoyan on 2017/8/16.
 */
interface DownloadListener {
    fun onDownLoaderListener(currentLength: Int, status: Int)
}