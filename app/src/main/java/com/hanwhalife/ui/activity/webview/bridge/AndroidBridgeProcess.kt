@file:Suppress("unused")

package com.hanwhalife.ui.activity.webview.bridge

import android.app.Activity
import android.webkit.WebView
import androidx.navigation.NavController
import com.snc.zero.ui.kotlin.extentions.popup
import org.json.JSONObject
import timber.log.Timber

@Suppress("unused")
object AndroidBridgeProcess {

    @JvmStatic
    fun webClose(
        webView: WebView,
        args: JSONObject?,
        callback: String?,
        navController: NavController? = null,
    ) {
        Timber.i("[AndroidBridge] callNativeMethod: webClose($args, \"$callback\")")
        webView.post {
            if (webView.canGoBack()) {
                webView.goBack()
                return@post
            }

            navController?.let {
                it.popup()
                return@post
            }

            if (webView.context is Activity) {
                (webView.context as Activity).finish()
            }
        }
    }
}