package com.hanwhalife.ui.activity.webview.download

import android.content.Context
import android.net.Uri
import android.webkit.DownloadListener
import com.snc.zero.lib.kotlin.download.DownloadManager
import timber.log.Timber

class BizDownloadListener(private val context: Context) : DownloadListener {
    override fun onDownloadStart(
        url: String,
        userAgent: String,
        contentDisposition: String,
        mimetype: String,
        contentLength: Long
    ) {
        DownloadManager().downloadIt(
            context,
            url,
            mimetype,
            object : DownloadManager.OnDownloadListener {
                override fun onDownloadDone(contentUri: Uri?) {
                    Timber.i("onDownloadDone: $contentUri")
                }

                override fun onDownloadFailed(message: String?) {
                    Timber.i("onDownloadFailed: $message")
                }

            })
    }
}