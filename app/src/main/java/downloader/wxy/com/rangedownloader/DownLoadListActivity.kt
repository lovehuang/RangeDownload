package downloader.wxy.com.rangedownloader

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_download.*
import kotlinx.android.synthetic.main.content_download.*
import kotlinx.android.synthetic.main.item_view.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startService
import org.jetbrains.anko.toast


class DownLoadListActivity : AppCompatActivity() {

    // 循环下载这三个,如果有重复则重命名之后下载
    private val urlList = mapOf(
            "Video1" to "http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4",
            "Video2" to "http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_2mb.mp4",
            "Video3" to "http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_5mb.mp4"
    )

    /**
     * 最多同时有几个任务
     */
    private val MAX_DOWNLOAD_COUNT = 3

    private var mData: MutableList<DownLoadEntity> = mutableListOf()

    private lateinit var mDownLoadService: DownloadService
    private lateinit var mServiceConnection: ServiceConnection
    private lateinit var mAdapter: MyRecycleViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        val titles = urlList.keys.toTypedArray()
        mData = getDownLoadList(this)
        fab.onClick {
            val builder = AlertDialog.Builder(this@DownLoadListActivity)
            builder.setTitle("下载哪一个比较好呢")
            builder.setItems(titles, { _, which ->
                if (mData.size >= MAX_DOWNLOAD_COUNT) {
                    //TODO 完成的任务不应该占用任务池
                    toast("最多只允许 $MAX_DOWNLOAD_COUNT 个任务")
                    return@setItems
                }
                val name = titles[which] as String
                mData.forEach {
                    if (it.name == name) {
                        toast("$name 任务已存在")
                        return@setItems
                    }
                }
                toast(name + " Download Start!")
                mData.add(DownLoadEntity(name = name, url = urlList[name] as String))
                mAdapter.notifyDataSetChanged()
            })
            builder.create().show()
        }

        recycleview.layoutManager = LinearLayoutManager(this)
        mAdapter = MyRecycleViewAdapter(mData)
        recycleview.adapter = mAdapter
        startService<DownloadService>()

        mServiceConnection =
                object : ServiceConnection {
                    override fun onServiceDisconnected(name: ComponentName) {}

                    override fun onServiceConnected(name: ComponentName, service: IBinder) {
                        val sBinder = service as DownloadService.DownloadBinder
                        mDownLoadService = sBinder.service
                    }
                }

        bindService(Intent(this, DownloadService::
        class.java), mServiceConnection, Context.BIND_AUTO_CREATE)

    }


    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        saveDownLoadList(this, mData)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        mData = getDownLoadList(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mServiceConnection)
        saveDownLoadList(this, mData)
    }

    inner class MyRecycleViewAdapter(private val mDataSource: MutableList<DownLoadEntity>) : RecyclerView.Adapter<MyRecycleViewAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.setData(mDataSource[position], position)
        }

        override fun getItemCount(): Int {
            return mDataSource.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), DownloadListener {

            lateinit var mEntity: DownLoadEntity

            fun setData(entity: DownLoadEntity, position: Int) {
                mEntity = entity
                itemView.name.text = entity.name

                itemView.status.text = getStatusString(entity.status)
                if (entity.status == DownLoadEntity.STATUS_SUCCEED) {
                    itemView.status.isEnabled = false
                } else {
                    itemView.status.onClick {
                        when (entity.status) {
                            DownLoadEntity.STATUS_IDLE, DownLoadEntity.STATUS_FAILED -> entity.status = DownLoadEntity.STATUS_DOWNLOADLING
                            DownLoadEntity.STATUS_DOWNLOADLING -> entity.status = DownLoadEntity.STATUS_IDLE
                        }
                        itemView.status.text = getStatusString(entity.status)
                        mDownLoadService.updateDownLoad(entity, this@ViewHolder)
                    }
                    mDownLoadService.updateDownLoad(entity, this)
                }

                itemView.delete.onClick {
                    val entity = mDataSource.removeAt(position)
                    entity.status = DownLoadEntity.STATUS_DELETE
                    notifyItemRemoved(position)
                    mDownLoadService.updateDownLoad(entity, this@ViewHolder)
                }

                itemView.progressBar.max = entity.totalLength
                itemView.progressBar.progress = entity.currentLength
            }

            override fun onDownLoaderListener(currentLength: Int, status: Int) {
                itemView.status.text = getStatusString(status)
                itemView.progressBar.progress = currentLength
            }

            fun getStatusString(status: Int): String {
                when (status) {
                    DownLoadEntity.STATUS_IDLE -> return "开始"
                    DownLoadEntity.STATUS_DOWNLOADLING -> return "暂停"
                    DownLoadEntity.STATUS_SUCCEED -> return "下载成功"
                    DownLoadEntity.STATUS_FAILED -> return "重新开始"
                    DownLoadEntity.STATUS_DELETE -> return "已删除"
                }
                return "程序出错"
            }
        }
    }
}