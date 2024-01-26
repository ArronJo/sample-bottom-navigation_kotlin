package com.snc.utils.extenstions

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
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
import com.snc.configs.AppConfig
import com.snc.ui.activity.webview.chooser.listener.FileChooserListener
import com.snc.ui.activity.webview.client.BizWebChromeClient
import com.snc.ui.activity.webview.client.BizWebViewClient
import com.snc.ui.activity.webview.client.OnLifeCycleListener
import com.snc.ui.activity.webview.download.BizDownloadListener
import com.snc.sample.bottom_navigation_kotlin.BuildConfig
import com.snc.sample.bottom_navigation_kotlin.R
import com.snc.zero.lib.kotlin.permission.RPermission
import com.snc.zero.lib.kotlin.permission.RPermissionListener
import com.snc.zero.ui.kotlin.extentions.setProgressWithAnim
import com.snc.zero.ui.kotlin.util.IntentUtil
import com.snc.zero.ui.kotlin.util.ProgressBarUtil
import timber.log.Timber

private val gHandler = Handler(Looper.getMainLooper())

@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
fun WebView.setup(
    activity: Activity,
    bizWebViewClient: BizWebViewClient? = null,
    bizWebChromeClient: BizWebChromeClient? = null,
    fileChooserListener: FileChooserListener? = null,
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

        clearCacheWithDB()
    }

    progressBar?.let {
        it.setProgressWithAnim(0)
        it.max = 100

        //it.unit = 10
        //it.setMaxWithUnit(100)
        //it.setProgressWithUnit(0)
    }

    webViewClient = bizWebViewClient ?: BizWebViewClient(activity, this, object : OnLifeCycleListener {
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
                    ProgressBarUtil.stopAnimLoop()
                    it.visibility = View.GONE
                }, 500)
            }

            listener?.onPageFinished(view, url)
        }
    })

    val chromeClient = bizWebChromeClient ?: BizWebChromeClient(activity, this, object : OnLifeCycleListener {
        override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
        }

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            progressBar?.let {
                if (newProgress >= 70) {
                    ProgressBarUtil.startAnimLoop(it,
                        duration = 1800,
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
    chromeClient.fileChooserListener = fileChooserListener
    webChromeClient = chromeClient

    setDownloadListener(BizDownloadListener(context))
}

fun WebView.clearCacheWithDB() {
    deleteDatabaseFile("webview.db")
    deleteDatabaseFile("webviewCache.db")
    try {
        clearCache(true)
    } catch (e: Exception) {
        Timber.e(e)
    }
}

fun WebView.deleteDatabaseFile(dbName: String) {
    try {
        val dbFile = context.getDatabasePath(dbName)
        if (null != dbFile && dbFile.exists()) {
            if (!context.deleteDatabase(dbName)) {
                Timber.e("##### $dbName :An error occurred while deleting the file. Failed to delete file. !!!")
            }
        }
    } catch (e: Exception) {
        Timber.e(e)
    }
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

fun WebView.loadUrlWithHeader(activity: Activity, uriString: String, headers: Map<String, String>? = null) {
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

        RPermission.with(activity)
            .setPermissions(permissions)
            .setPermissionListener(object : RPermissionListener {
                override fun onPermissionGranted(grantPermissions: List<String>) {
                    Timber.i("[WEBVIEW] onPermissionGranted()...$grantPermissions")
                    loadUrl(uriString, extraHeaders)
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    Timber.w("[WEBVIEW] onPermissionDenied()...$deniedPermissions")

                    IntentUtil.showGotoSettingsDialog(activity = activity, msg = deniedPermissions)
                }

                override fun onPermissionRationaleShouldBeShown(deniedPermissions: List<String>) {
                    Timber.w("[WEBVIEW] onPermissionRationaleShouldBeShown()...$deniedPermissions")

                    IntentUtil.showGotoSettingsDialog(activity = activity, msg = deniedPermissions)
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
