package com.snc.ui.activity.webview.chooser

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import com.snc.ui.activity.webview.chooser.listener.FileChooserListener
import com.snc.zero.lib.kotlin.extentions.getMediaDir
import com.snc.zero.lib.kotlin.util.DateTimeFormat
import com.snc.zero.lib.kotlin.util.UriUtil
import timber.log.Timber
import java.io.File
import java.util.Date

class BizFileChooserListener(private val fileChooserActivityResultLauncher: ActivityResultLauncher<Intent>) :
    FileChooserListener {

    private var uploadMessage: ValueCallback<Array<Uri>>? = null
    private var mediaURIs: Array<Uri?>? = null

    companion object {
        private const val ALL_TYPE = "image/*|audio/*|video/*"

        private const val IMAGE_TYPE = 0
        private const val AUDIO_TYPE = 1
        private const val VIDEO_TYPE = 2
    }

    override fun onOpenFileChooser(
        webView: WebView,
        filePathCallback: ValueCallback<Array<Uri>>?,
        acceptTypes: Array<String>?
    ) {
        Timber.i("FileChooser::onOpenFileChooser")

        this.uploadMessage = filePathCallback

        var acceptType = ""
        acceptTypes?.let {
            for (type in it) {
                if (TextUtils.isEmpty(type)) {
                    continue
                }
                if (!type.startsWith("image/")
                    && !type.startsWith("audio/")
                    && !type.startsWith("video/")
                    && !type.startsWith("application/")
                ) {
                    continue
                }
                acceptType += if (TextUtils.isEmpty(acceptType)) {
                    type
                } else {
                    ",$type"
                }
            }
        }

        if (acceptType.isEmpty() || "*/*".equals(acceptType, ignoreCase = true)) {
            acceptType = ALL_TYPE
        }

        try {
            mediaURIs = arrayOfNulls(3)

            val intentList: MutableList<Intent> = ArrayList()
            val fileName: String = DateTimeFormat.format(Date(), "yyyyMMdd_HHmmss")

            if (acceptType.contains("image/")) {
                mediaURIs?.let {
                    it[IMAGE_TYPE] = UriUtil.fromFile(
                        webView.context, File(
                            webView.context.getMediaDir("image"),
                            "$fileName.jpg"
                        )
                    )

                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                        putExtra(MediaStore.EXTRA_OUTPUT, it[IMAGE_TYPE])
                    }
                    intentList.add(intent)
                }
            }
            if (acceptType.contains("audio/")) {
                mediaURIs?.let {
                    it[AUDIO_TYPE] = UriUtil.fromFile(
                        webView.context, File(
                            webView.context.getMediaDir("audio"),
                            "$fileName.m4a"
                        )
                    )

                    val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION).apply {
                        putExtra(MediaStore.EXTRA_OUTPUT, it[AUDIO_TYPE])
                    }
                    intentList.add(intent)
                }
            }
            if (acceptType.contains("video/")) {
                mediaURIs?.let {
                    it[VIDEO_TYPE] = UriUtil.fromFile(
                        webView.context, File(
                            webView.context.getMediaDir("video"),
                            "$fileName.mp4"
                        )
                    )
                    val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
                        putExtra(MediaStore.EXTRA_OUTPUT, it[VIDEO_TYPE])
                    }
                    intentList.add(intent)
                }
            }

            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)

                type = if (ALL_TYPE == acceptType) {
                    "*/*"
                } else {
                    acceptType
                }
            }

            val chooserIntent = Intent.createChooser(intent, "Chooser")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toTypedArray())
            fileChooserActivityResultLauncher.launch(chooserIntent)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun onActivityResultFileChooser(result: ActivityResult) {
        Timber.i("FileChooser::onActivityResultFileChoose: resultCode[${result.resultCode}] data[${result.data}]")

        if (null == uploadMessage) {
            Timber.i("[WEBVIEW] onActivityResultLollipop(): filePathCallbackLollipop is null !!!")
            return
        }

        if (Activity.RESULT_OK != result.resultCode) {
            uploadMessage?.onReceiveValue(null)
            uploadMessage = null
            return
        }

        try {
            if (null != result.data) {
                uploadMessage?.onReceiveValue(
                    WebChromeClient.FileChooserParams.parseResult(
                        result.resultCode,
                        result.data
                    )
                )
            } else {
                val results: MutableList<Uri> = java.util.ArrayList()
                mediaURIs?.let {
                    it[IMAGE_TYPE]?.let { iit ->
                        results.add(iit)
                    }
                    it[AUDIO_TYPE]?.let { iit ->
                        results.add(iit)
                    }
                    it[VIDEO_TYPE]?.let { iit ->
                        results.add(iit)
                    }
                }
                uploadMessage?.onReceiveValue(results.toTypedArray())
            }
        } catch (e: java.lang.Exception) {
            Timber.e(e)
        } finally {
            uploadMessage = null
            mediaURIs = null
        }
    }

}