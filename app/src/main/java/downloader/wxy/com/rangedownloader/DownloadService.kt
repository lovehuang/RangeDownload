package downloader.wxy.com.rangedownloader

/**
 * Created by wangxiaoyan on 2017/8/20.
 */

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder


/**
 * Created by dk on 2016/11/25.
 */

class DownloadService : Service() {
    inner class DownloadBinder : Binder() {
        val service: DownloadService
            get() = this@DownloadService
    }

    var sBinder = DownloadBinder()

    override fun onBind(intent: Intent): IBinder? {
        return sBinder
    }

    fun updateDownLoad(entity: DownLoadEntity, listener: DownloadListener) {

    }
}

