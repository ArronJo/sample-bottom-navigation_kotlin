package com.snc.ui.activity.webview.client

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.webkit.*
import android.widget.Toast
import androidx.webkit.SafeBrowsingResponseCompat
import androidx.webkit.WebResourceErrorCompat
import androidx.webkit.WebViewAssetLoader.*
import androidx.webkit.WebViewClientCompat
import androidx.webkit.WebViewFeature
import com.snc.utils.extenstions.ASSET_BASE_DOMAIN
import com.snc.utils.extenstions.ASSET_PATH
import com.snc.utils.extenstions.INTERNAL_PATH
import com.snc.utils.extenstions.RES_PATH
import com.snc.zero.lib.kotlin.extentions.getInternalFilesDir
import com.snc.zero.ui.kotlin.dialog.ModalBuilder
import timber.log.Timber

class BizWebViewClient(
    private val activity: Activity,
    webView: WebView,
    private val listener: OnLifeCycleListener? = null
) : WebViewClientCompat() {

    private var assetLoader: androidx.webkit.WebViewAssetLoader = Builder()
        .setHttpAllowed(true)
        .setDomain(ASSET_BASE_DOMAIN)
        .addPathHandler(ASSET_PATH, AssetsPathHandler(webView.context))
        .addPathHandler(RES_PATH, ResourcesPathHandler(webView.context))
        .addPathHandler(
            INTERNAL_PATH,
            InternalStoragePathHandler(
                webView.context,
                webView.context.getInternalFilesDir("public")
            )
        )
        .build()

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        val uri = request.url

        try {
            return assetLoader.shouldInterceptRequest(uri)
        } catch (e: Exception) {
            Timber.w(e)
        }
        return super.shouldInterceptRequest(view, request)
    }

    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        listener?.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView, url: String?) {
        super.onPageFinished(view, url)
        listener?.onPageFinished(view, url)
    }

    override fun onReceivedHttpError(
        view: WebView,
        request: WebResourceRequest,
        errorResponse: WebResourceResponse
    ) {
        val url = request.url.toString()

        val buff = StringBuilder()
        buff.append("\n  encoding[").append(errorResponse.encoding).append("]  ")
        buff.append("\n  mimeType[").append(errorResponse.mimeType).append("]  ")

        buff.append("\n  method[").append(request.method).append("]  ")
        buff.append("\n  headers[\n").append(request.requestHeaders.toString()).append("]  ")
        buff.append("\n  statusCode[").append(errorResponse.statusCode).append("]  ")
        buff.append("\n  responseHeaders[").append(errorResponse.responseHeaders).append("]  ")
        buff.append("\n  reasonPhrase[").append(errorResponse.reasonPhrase).append("]  ")

        Timber.i(
            "[WebView::WebViewClient] onReceivedHttpError() :  request = $url" +
                    "errorResponse = $buff"
        )
        super.onReceivedHttpError(view, request, errorResponse)
    }

    @SuppressLint("RequiresFeature")
    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceErrorCompat
    ) {
        val reqUrl = request.url.toString()

        Timber.e(
            "[WebView::WebViewClient] onReceivedError : url = $reqUrl" +
                    ", errorCode = ${error.errorCode}" +
                    ", description = ${error.description}"
        )

        when (error.errorCode) {
            ERROR_AUTHENTICATION -> {}
            ERROR_BAD_URL -> {}
            ERROR_CONNECT -> {}
            ERROR_FAILED_SSL_HANDSHAKE -> {}
            ERROR_FILE -> {}
            ERROR_FILE_NOT_FOUND -> {}
            ERROR_HOST_LOOKUP -> {}
            ERROR_IO -> {}
            ERROR_PROXY_AUTHENTICATION -> {}
            ERROR_REDIRECT_LOOP -> {}
            ERROR_TIMEOUT -> {}
            ERROR_TOO_MANY_REQUESTS -> {}
            ERROR_UNKNOWN -> {}
            ERROR_UNSUPPORTED_AUTH_SCHEME -> {}
            ERROR_UNSUPPORTED_SCHEME -> {}
            ERROR_UNSAFE_RESOURCE -> {}
            else -> {}
        }

        super.onReceivedError(view, request, error)
    }

    override fun onSafeBrowsingHit(
        view: WebView,
        request: WebResourceRequest,
        threatType: Int,
        callback: SafeBrowsingResponseCompat
    ) {
        Timber.i("[WebView::WebViewClient] onSafeBrowsingHit() :  request = $request")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_RESPONSE_BACK_TO_SAFETY)) {
                callback.backToSafety(true)
                Toast.makeText(view.context, "Unsafe web page blocked.", Toast.LENGTH_LONG).show()
            }

            super.onSafeBrowsingHit(view, request, threatType, callback)
        }
    }

    @SuppressLint("WebViewClientOnReceivedSslError")
    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        val msg = when (error.primaryError) {
            SslError.SSL_NOTYETVALID -> view.context.resources.getString(com.snc.zero.resources.R.string.msg_the_certificate_is_not_yet_valid)
            SslError.SSL_EXPIRED -> view.context.resources.getString(com.snc.zero.resources.R.string.msg_the_certificate_has_expired)
            SslError.SSL_IDMISMATCH -> view.context.resources.getString(com.snc.zero.resources.R.string.msg_hostname_mismatch)
            SslError.SSL_UNTRUSTED -> view.context.resources.getString(com.snc.zero.resources.R.string.msg_the_certificate_authority_is_not_trusted)
            SslError.SSL_DATE_INVALID -> view.context.resources.getString(com.snc.zero.resources.R.string.msg_certificate_date_is_invalid)
            SslError.SSL_INVALID -> view.context.resources.getString(com.snc.zero.resources.R.string.msg_certificate_date_is_invalid)
            else -> view.context.resources.getString(com.snc.zero.resources.R.string.msg_a_generic_error_occurred)
        }

        ModalBuilder.with(activity)
            .setTitle(com.snc.zero.resources.R.string.notice)
            .setMessage("$msg\n${view.context.resources.getString(com.snc.zero.resources.R.string.msg_do_you_want_to_continue)}")
            .setPositiveButton(com.snc.zero.resources.R.string.yes) { _, _ -> handler.proceed() }
            .setNegativeButton(com.snc.zero.resources.R.string.close) { _, _ -> handler.cancel() }
            .show()
    }
}

interface OnLifeCycleListener {
    fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?)
    fun onProgressChanged(view: WebView, newProgress: Int)
    fun onPageFinished(view: WebView, url: String?)
}