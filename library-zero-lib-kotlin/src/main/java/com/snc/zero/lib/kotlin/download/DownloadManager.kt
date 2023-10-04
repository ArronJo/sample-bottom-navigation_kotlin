package com.snc.zero.lib.kotlin.download

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import com.snc.zero.lib.kotlin.mimetype.MimeTypes
import timber.log.Timber

class DownloadManager {

    private var downloadId: Long = 0

    fun downloadIt(
        context: Context,
        urlString: String,
        mimeType: String?,
        listener: OnDownloadListener? = null
    ) {
        Timber.i("[DownloadManager] downloadIt()")

        try {
            val request = DownloadManager.Request(Uri.parse(urlString))

            request.addRequestHeader("Content-Type", "application/x-www-form-urlencoded")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                @Suppress("DEPRECATION")
                request.allowScanningByMediaScanner()
            }

            val subPathWithFileName = urlString.substring(urlString.lastIndexOf("/"))
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                subPathWithFileName
            )
            if (mimeType.isNullOrEmpty()) {
                request.setMimeType(MimeTypes.getMimeTypeFromFileName(subPathWithFileName))
            } else {
                request.setMimeType(mimeType)
            }

            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            this.downloadId = downloadManager.enqueue(request)

            this.registerDownloadReceiver(context, listener)

        } catch (e: Exception) {
            Timber.e(e, "Exception")
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerDownloadReceiver(context: Context, listener: OnDownloadListener? = null) {
        val intentFilter = IntentFilter()
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)

        context.registerReceiver(object : BroadcastReceiver() {
            @SuppressLint("Range")
            override fun onReceive(context: Context, intent: Intent) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent.action) {
                    val downloadManager =
                        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

                    val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (downloadId == id) {
                        val query = DownloadManager.Query().setFilterById(id)
                        val cursor = downloadManager.query(query)
                        if (!cursor.moveToFirst()) {
                            return
                        }

                        val status =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                        if (DownloadManager.STATUS_SUCCESSFUL == status) {
                            val contentUri = downloadManager.getUriForDownloadedFile(id)
                            listener?.onDownloadDone(contentUri)

                        } else if (DownloadManager.STATUS_FAILED == status) {
                            listener?.onDownloadFailed("download failed")
                            Toast.makeText(context, "download failed", Toast.LENGTH_SHORT).show()
                        }

                        unregisterDownloadReceiver(context, this)
                    }
                }
            }
        }, intentFilter)
    }

    private fun unregisterDownloadReceiver(context: Context, receiver: BroadcastReceiver) {
        context.unregisterReceiver(receiver)
    }

    interface OnDownloadListener {
        fun onDownloadDone(contentUri: Uri?)
        fun onDownloadFailed(message: String?)
    }
}
