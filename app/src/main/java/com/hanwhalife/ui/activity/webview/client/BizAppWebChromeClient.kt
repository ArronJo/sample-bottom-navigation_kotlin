package com.hanwhalife.ui.activity.webview.client

import android.app.Activity
import android.net.Uri
import android.os.Message
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.ConsoleMessage.MessageLevel
import android.webkit.GeolocationPermissions
import android.webkit.JsResult
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.snc.zero.ui.kotlin.dialog.ModalBuilder
import timber.log.Timber

class BizAppWebChromeClient(
    private val webView: WebView,
    private val listener: OnLifeCycleListener? = null
) : WebChromeClient() {

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>,
        fileChooserParams: FileChooserParams
    ): Boolean {
        Timber.i("[WebView::WebChromeClient] onShowFileChooser() : fileChooserParams = $fileChooserParams")
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
    }

    override fun onHideCustomView() {
        Timber.i("[WebView::WebChromeClient] onHideCustomView()")
        super.onHideCustomView()
    }

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        Timber.i("[WebView::WebChromeClient] onShowCustomView() : callback = $callback")
        super.onShowCustomView(view, callback)
    }

    override fun onCloseWindow(window: WebView?) {
        Timber.i("[WebView::WebChromeClient] onCloseWindow() : window = $window")
        super.onCloseWindow(window)
    }

    override fun onCreateWindow(
        view: WebView?,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?
    ): Boolean {
        Timber.i("[WebView::WebChromeClient] onCreateWindow() : isDialog = $isDialog, isUserGesture = $isUserGesture, resultMsg = $resultMsg")
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
    }

    override fun onGeolocationPermissionsHidePrompt() {
        Timber.i("[WebView::WebChromeClient] onGeolocationPermissionsHidePrompt()")
        super.onGeolocationPermissionsHidePrompt()
    }

    override fun onGeolocationPermissionsShowPrompt(
        origin: String?,
        callback: GeolocationPermissions.Callback?
    ) {
        Timber.i("[WebView::WebChromeClient] onGeolocationPermissionsShowPrompt() : origin = $origin")
        super.onGeolocationPermissionsShowPrompt(origin, callback)
    }

    override fun onPermissionRequest(request: PermissionRequest?) {
        Timber.i("[WebView::WebChromeClient] onPermissionRequest() : request = $request")
        super.onPermissionRequest(request)
    }

    override fun onPermissionRequestCanceled(request: PermissionRequest?) {
        Timber.i("[WebView::WebChromeClient] onPermissionRequestCanceled() : request = $request")
        super.onPermissionRequestCanceled(request)
    }

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        Timber.i("[WebView::WebChromeClient] onProgressChanged() : newProgress = $newProgress")
        listener?.onProgressChanged(webView, newProgress)
        super.onProgressChanged(view, newProgress)
    }

    override fun onJsAlert(
        view: WebView,
        url: String,
        message: String,
        result: JsResult
    ): Boolean {
        ModalBuilder.with(view.context as Activity)
            .setTitle(com.snc.zero.resources.R.string.web)
            .setMessage(message)
            .setPositiveButton(com.snc.zero.resources.R.string.ok) { _, _ -> result.confirm() }
            .show()
        return true
    }

    override fun onJsConfirm(
        view: WebView,
        url: String,
        message: String,
        result: JsResult
    ): Boolean {
        ModalBuilder.with(view.context as Activity)
            .setTitle(com.snc.zero.resources.R.string.web)
            .setMessage(message)
            .setPositiveButton(com.snc.zero.resources.R.string.yes) { _, _ -> result.confirm() }
            .setNegativeButton(com.snc.zero.resources.R.string.no) { _, _ -> result.cancel() }
            .show()
        return true
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
        val level = consoleMessage.messageLevel()
        val message = consoleMessage.message()
        when (level) {
            MessageLevel.ERROR -> {
                Timber.e(message)
            }
            MessageLevel.WARNING -> {
                Timber.w(message)
            }
            MessageLevel.DEBUG -> {
                Timber.d(message)
            }
            else -> {
                Timber.i(message)
            }
        }
        return true
    }
}