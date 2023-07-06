package com.snc.ui.activity.webview.chooser.listener

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebView
import androidx.activity.result.ActivityResult

interface FileChooserListener {

    fun onOpenFileChooser(
        webView: WebView,
        filePathCallback: ValueCallback<Array<Uri>>,
        acceptTypes: Array<String>
    )

    fun onActivityResultFileChooser(result: ActivityResult)
}