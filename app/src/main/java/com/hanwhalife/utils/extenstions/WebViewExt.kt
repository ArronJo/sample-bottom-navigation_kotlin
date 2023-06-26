package com.hanwhalife.utils.extenstions

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ProgressBar
import com.hanwhalife.consts.AppConfig
import com.hanwhalife.ui.activity.webview.client.BizAppWebChromeClient
import com.hanwhalife.ui.activity.webview.client.BizWebViewClient
import com.hanwhalife.ui.activity.webview.client.OnLifeCycleListener
import com.hanwhalife.ui.activity.webview.download.BizDownloadListener
import com.snc.sample.bottom_navigation_kotlin.BuildConfig
import com.snc.sample.bottom_navigation_kotlin.R
import com.snc.zero.lib.kotlin.permission.RPermission
import com.snc.zero.lib.kotlin.permission.RPermissionListener
import com.snc.zero.lib.kotlin.util.IntentSettings
import com.snc.zero.ui.kotlin.dialog.ModalBuilder
import com.snc.zero.ui.kotlin.extentions.setProgressWithAnim
import com.snc.zero.ui.kotlin.extentions.startAnimLoop
import com.snc.zero.ui.kotlin.extentions.stopAnimLoop
import timber.log.Timber

private val gHandler = Handler(Looper.getMainLooper())

@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
fun WebView.setup(
    bizWebViewClient: BizWebViewClient? = null,
    bizWebChromeClient: BizAppWebChromeClient? = null,
    listener: OnLifeCycleListener? = null,
    progressBar: ProgressBar? = null
) {
    setTransparentBackground()

    settings.apply {
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        setInitialScale(0)
        scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        isScrollbarFadingEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_BOUND, true)
        }
        allowFileAccess = true
        allowContentAccess = true
        //allowFileAccessFromFileURLs = true
        //allowUniversalAccessFromFileURLs = true
        blockNetworkImage = false
        blockNetworkLoads = false
        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            safeBrowsingEnabled = false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            disabledActionModeMenuItems = WebSettings.MENU_ITEM_NONE
        }
        javaScriptEnabled = true
        javaScriptCanOpenWindowsAutomatically = false

        cacheMode = WebSettings.LOAD_NO_CACHE
        databaseEnabled = true
        domStorageEnabled = true
        loadsImagesAutomatically = true
        loadWithOverviewMode = false

        offscreenPreRaster = false

        setSupportMultipleWindows(false)
        useWideViewPort = true
        setSupportZoom(true)
        builtInZoomControls = true
        displayZoomControls = false
        setGeolocationEnabled(true)
        defaultTextEncodingName = "utf-8"
        mediaPlaybackRequiresUserGesture = true

        setNeedInitialFocus(true)

        userAgentString = makeUserAgent()
    }

    progressBar?.let {
        it.setProgressWithAnim(0)
        it.max = 100

        //it.unit = 10
        //it.setMaxWithUnit(100)
        //it.setProgressWithUnit(0)
    }

    webViewClient = bizWebViewClient ?: BizWebViewClient(this, object : OnLifeCycleListener {
        override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
            listener?.onPageStarted(view, url, favicon)
        }

        override fun onProgressChanged(view: WebView, newProgress: Int) {
        }

        override fun onPageFinished(view: WebView, url: String?) {
            progressBar?.let {
                it.setProgressWithAnim(100)
                //it.setProgressWithUnit(100)

                gHandler.postDelayed({
                    it.stopAnimLoop()
                    it.visibility = View.GONE
                }, 500)
            }

            listener?.onPageFinished(view, url)
        }
    })

    webChromeClient = bizWebChromeClient ?: BizAppWebChromeClient(this, object : OnLifeCycleListener {
        override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
        }

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            progressBar?.let {
                if (newProgress >= 70) {
                    it.startAnimLoop(duration = 1800,
                        repeatCount = 0,
                        listener = { state ->
                            if ("end" == state) {
                                gHandler.postDelayed({
                                    it.visibility = View.GONE
                                }, 500)
                            }
                        })
                } else {
                    it.setProgressWithAnim(newProgress)
                    //it.setProgressWithUnit(newProgress)
                    //it.animationIncrement(newProgress)
                }
            }

            listener?.onProgressChanged(view, newProgress)
        }

        override fun onPageFinished(view: WebView, url: String?) {
        }
    })

    setDownloadListener(BizDownloadListener(context))
}

const val SCHEME_HTTP = "http://"
const val SCHEME_HTTPS = "https://"
const val SCHEME_FILE = "file://"
const val SCHEME_ASSET = "file:///android_asset"
const val ASSET_BASE_DOMAIN = AppConfig.ASSET_BASE_DOMAIN
const val ASSET_PATH = AppConfig.ASSET_PATH
const val RES_PATH = AppConfig.RES_PATH
const val INTERNAL_PATH = AppConfig.INTERNAL_PATH
const val SCHEME_ASSET_API30 = SCHEME_HTTPS + AppConfig.ASSET_BASE_DOMAIN + AppConfig.ASSET_PATH
//const val SCHEME_RES = "file:///android_res"
//const val SCHEME_RES_API30 = SCHEME_HTTPS + BuildConfig.ASSET_BASE_DOMAIN + BuildConfig.RES_PATH

fun WebView.loadUrlWithHeader(uriString: String, headers: Map<String, String>? = null) {
    val extraHeaders = HashMap<String, String>()
    headers?.let {
        for ((key, value) in headers) {
            extraHeaders[key] = value
        }
    }

    if (uriString.startsWith(SCHEME_HTTP) || uriString.startsWith(SCHEME_HTTPS)
        || uriString.startsWith(SCHEME_ASSET) || uriString.startsWith(SCHEME_ASSET_API30)
    ) {
        loadUrl(uriString, extraHeaders)
        return
    }

    if (uriString.startsWith(SCHEME_FILE)) {
        val permissions: ArrayList<String> = arrayListOf()
        // Dangerous Permission
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        RPermission.with(context)
            .setPermissions(permissions)
            .setPermissionListener(object : RPermissionListener {
                override fun onPermissionGranted(grantPermissions: List<String>) {
                    Timber.i("[WEBVIEW] onPermissionGranted()...$grantPermissions")
                    loadUrl(uriString, extraHeaders)
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    Timber.e("[WEBVIEW] onPermissionDenied()...$deniedPermissions")

                    ModalBuilder.with(context)?.apply {
                            this.setTitle(com.snc.zero.resources.R.string.notice)
                            this.setMessage("${context.resources.getString(com.snc.zero.resources.R.string.msg_please_allow_all_required_permissions)}\n$deniedPermissions")
                            this.setPositiveButton(com.snc.zero.resources.R.string.settings) { _: DialogInterface?, _: Int ->
                                IntentSettings.manageAppSettings(
                                    context,
                                    context.packageName
                                )
                            }
                            .show()
                        }
                }

                override fun onPermissionRationaleShouldBeShown(deniedPermissions: List<String>) {
                    Timber.e("[WEBVIEW] onPermissionRationaleShouldBeShown()...$deniedPermissions")

                    ModalBuilder.with(context)?.apply {
                        this.setTitle(com.snc.zero.resources.R.string.notice)
                        this.setMessage("${context.resources.getString(com.snc.zero.resources.R.string.msg_please_allow_all_required_permissions)}\n$deniedPermissions")
                        this.setPositiveButton(com.snc.zero.resources.R.string.settings) { _: DialogInterface?, _: Int ->
                            IntentSettings.manageAppSettings(
                                context,
                                context.packageName
                            )
                        }
                        .show()
                    }
                }
            })
            .check()
    }
}

fun WebView.makeUserAgent(): String {
    var ua: String = settings.userAgentString.trim { it <= ' ' }
    try {
        ua += " SNC_SAMPLE/${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"
        ua += " ("
        var desc = resources.getString(R.string.flavors) + "; "
        if (BuildConfig.DEBUG) {
            desc += "switch; "
        }
        ua += desc.trim { it <= ' ' }
        ua += ")"

        return ua

    } catch (e: PackageManager.NameNotFoundException) {
        Timber.e(e)
    }
    return ua
}

fun WebView.setTransparentBackground() {
    setBackgroundColor(Color.TRANSPARENT)
}
