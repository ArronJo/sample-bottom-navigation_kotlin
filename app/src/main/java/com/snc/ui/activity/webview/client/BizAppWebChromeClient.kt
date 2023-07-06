package com.snc.ui.activity.webview.client

import android.Manifest
import android.app.Activity
import android.net.Uri
import android.os.Build
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
import com.snc.ui.activity.webview.chooser.listener.FileChooserListener
import com.snc.zero.lib.kotlin.permission.RPermission
import com.snc.zero.lib.kotlin.permission.RPermissionListener
import com.snc.zero.ui.kotlin.dialog.ModalBuilder
import com.snc.zero.ui.kotlin.util.IntentUtil
import timber.log.Timber

class BizAppWebChromeClient(
    private val activity: Activity,
    private val webView: WebView,
    private val listener: OnLifeCycleListener? = null
) : WebChromeClient() {

    var fileChooserListener: FileChooserListener? = null

    override fun onShowFileChooser(
        webView: WebView,
        filePathCallback: ValueCallback<Array<Uri>>,
        fileChooserParams: FileChooserParams
    ): Boolean {
        Timber.i("[WebView::WebChromeClient] onShowFileChooser()::\nfilePathCallback[$filePathCallback]  fileChooserParams[$fileChooserParams]")

        val permissions = arrayListOf<String>()
        permissions.add(Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        RPermission.with(activity)
            .setPermissions(permissions)
            .setPermissionListener(object : RPermissionListener {
                override fun onPermissionGranted(grantPermissions: List<String>) {
                    Timber.i("[WEBVIEW] onPermissionGranted()")
                    val acceptTypes = fileChooserParams.acceptTypes
                    fileChooserListener?.onOpenFileChooser(
                        webView,
                        filePathCallback,
                        acceptTypes
                    )
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    Timber.w("[WEBVIEW] onPermissionDenied()...$deniedPermissions")

                    IntentUtil.showGotoSettingsDialog(activity, activity.packageName, deniedPermissions)
                }

                override fun onPermissionRationaleShouldBeShown(deniedPermissions: List<String>) {
                    Timber.w("[WEBVIEW] onPermissionRationaleShouldBeShown()...$deniedPermissions")

                    IntentUtil.showGotoSettingsDialog(activity, activity.packageName, deniedPermissions)
                }
            })
            .check()
        return true
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

        val permissions = arrayListOf<String>()
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        RPermission.with(activity)
            .setPermissions(permissions)
            .setPermissionListener(object : RPermissionListener {
                override fun onPermissionGranted(grantPermissions: List<String>) {
                    Timber.i("onGeolocationPermissionsShowPrompt: onPermissionGranted() :: origin[$origin] callback[$callback]")
                    callback?.invoke(origin, true, false)
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    Timber.w("[WEBVIEW] onPermissionDenied() :: origin[$origin] callback[$callback]")

                    callback?.invoke(origin, false, false)
                }

                override fun onPermissionRationaleShouldBeShown(deniedPermissions: List<String>) {
                    Timber.w("[WEBVIEW] onPermissionRationaleShouldBeShown()...$deniedPermissions")

                    IntentUtil.showGotoSettingsDialog(activity, activity.packageName, deniedPermissions)
                }
            })
            .check()


        super.onGeolocationPermissionsShowPrompt(origin, callback)
    }

    override fun onPermissionRequest(request: PermissionRequest) {
        Timber.i("[WebView::WebChromeClient] onPermissionRequest() : request = $request")

        val origin = request.origin
        for (permission in request.resources) {
            when (permission) {
                PermissionRequest.RESOURCE_AUDIO_CAPTURE -> {
                    RPermission.with(activity)
                        .setPermissions( // Dangerous Permission
                            Manifest.permission.RECORD_AUDIO
                        )
                        .setPermissionListener(object : RPermissionListener {

                            override fun onPermissionGranted(grantPermissions: List<String>) {
                                Timber.i("[WEBVIEW] onPermissionGranted() : android.webkit.resource.AUDIO_CAPTURE :: origin[$origin] request[$request]")
                                request.grant(request.resources)
                            }

                            override fun onPermissionDenied(deniedPermissions: List<String>) {
                                Timber.w("[WEBVIEW] onPermissionDenied() : android.webkit.resource.AUDIO_CAPTURE :: origin[$origin] request[$request]")
                                request.deny()
                            }

                            override fun onPermissionRationaleShouldBeShown(deniedPermissions: List<String>) {
                                Timber.w("[WEBVIEW] onPermissionRationaleShouldBeShown()...$deniedPermissions")
                                request.deny()

                                IntentUtil.showGotoSettingsDialog(activity, activity.packageName, deniedPermissions)
                            }
                        })
                        .check()
                    return
                }

                PermissionRequest.RESOURCE_VIDEO_CAPTURE -> {
                    RPermission.with(activity)
                        .setPermissions( // Dangerous Permission
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO
                        )
                        .setPermissionListener(object : RPermissionListener {
                            override fun onPermissionGranted(grantPermissions: List<String>) {
                                Timber.i("[WEBVIEW] onPermissionGranted() : android.webkit.resource.VIDEO_CAPTURE :: origin[$origin] request[$request]")
                                request.grant(request.resources)
                            }

                            override fun onPermissionDenied(deniedPermissions: List<String>) {
                                Timber.w("[WEBVIEW] onPermissionDenied() : android.webkit.resource.VIDEO_CAPTURE :: origin[$origin] request[$request]")
                                request.deny()
                            }

                            override fun onPermissionRationaleShouldBeShown(deniedPermissions: List<String>) {
                                Timber.w("[WEBVIEW] onPermissionRationaleShouldBeShown()...$deniedPermissions")
                                request.deny()

                                IntentUtil.showGotoSettingsDialog(activity, activity.packageName, deniedPermissions)
                            }
                        })
                        .check()
                    return
                }
            }
        }
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

    override fun onJsAlert(
        view: WebView,
        url: String,
        message: String,
        result: JsResult
    ): Boolean {
        ModalBuilder.with(activity)
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
        ModalBuilder.with(activity)
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