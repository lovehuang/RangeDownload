package downloader.wxy.com.rangedownloader

/**
 * Created by wangxiaoyan on 2017/8/20.
 */

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.io.File


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

    private val mDownloadingEntity = mutableMapOf<String, Thread>()

    fun updateDownLoad(entity: DownLoadEntity, listener: DownloadListener) {
        when (entity.status) {
            DownLoadEntity.STATUS_DELETE -> { //删除
                mDownloadingEntity[entity.name]?.interrupt()
                val file = File(filesDir.toString() + entity.getFileName())
                if (file.exists()) file.delete()
                mDownloadingEntity.remove(entity.name)
            }

            DownLoadEntity.STATUS_IDLE -> {//停止下载
                mDownloadingEntity[entity.name]?.interrupt()
            }
            DownLoadEntity.STATUS_DOWNLOADLING -> {//开始下载
                var thread = DownLoadThread(this, entity, listener)
                thread.start()
                mDownloadingEntity.put(entity.name, thread)
            }
        }
    }
}

