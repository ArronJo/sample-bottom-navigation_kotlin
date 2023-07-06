package com.snc.ui.activity.webview.bridge

import android.net.Uri
import android.util.Base64
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebView
import androidx.navigation.NavController
import com.snc.zero.lib.kotlin.reflect.Reflector
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.net.URLDecoder

@Suppress("SameReturnValue", "unused")
class AndroidBridge(
    private val webView: WebView,
    private val navController: NavController? = null,
) {

    @JavascriptInterface
    fun callNativeMethod(urlString: String): Boolean {
        try {
            return executeProcess(webView, parse(urlString), navController)
        } catch (e: Exception) {
            Timber.e(Log.getStackTraceString(e))
        }
        return false
    }

    private fun executeProcess(
        webview: WebView,
        jsonObject: JSONObject,
        navController: NavController? = null,
    ): Boolean {
        try {
            var args: JSONObject? = null
            val command = jsonObject.getString("command")
            if (jsonObject.has("args")) {
                args = jsonObject.getJSONObject("args")
            }
            if (args == null) {
                args = JSONObject()
            }

            var callback: String? = null
            if (jsonObject.has("callbackScriptName")) {
                callback = jsonObject.getString("callbackScriptName")
            }

            val method = Reflector.getMethod(AndroidBridgeProcess, command)
            if (null == method) {
                Timber.e("method is null")
                return false
            }
            Reflector.invoke(AndroidBridgeProcess, method, webview, args, callback, navController)

        } catch (e: Exception) {
            Timber.e(Log.getStackTraceString(e))
        }
        return false
    }

    private fun parse(urlString: String): JSONObject {
        val uri = Uri.parse(urlString)
        val query = uri.encodedQuery
        try {
            query?.let {
                var tmp = String(Base64.decode(it, Base64.DEFAULT))
                tmp = URLDecoder.decode(tmp, "utf-8")
                return JSONObject(tmp)
            }
        } catch (e: Exception) {
            throw IOException("\"$query\" is not JSONObject.")
        }
        return JSONObject()
    }


    companion object {

        private const val SCHEME_JAVASCRIPT = "javascript:"

        @JvmStatic
        fun callJSFunction(webView: WebView, js: String, resultCallback: ValueCallback<String>?) {
            val buff = """(function() {
                          try {
                            $js
                          } catch(e) {
                            console.error('JS Error :', e.message);
                            return '[JS Error] ' + e.message;
                          }
                        })();"""

            webView.post { evaluateJavascript(webView, buff, resultCallback) }
        }

        @JvmStatic
        fun makeJavascript(functionName: String?, vararg params: String?): String {
            val buff = StringBuilder()
            buff.append("  ").append(functionName).append("(")
            for (i in params.indices) {
                val param: Any? = params[i]
                if (null == param) {
                    buff.append("''")
                } else {
                    buff.append("'").append(param).append("'")
                }
                if (i < params.size - 1) {
                    buff.append(", ")
                }
            }
            buff.append("); ")
            return buff.toString()
        }

        @JvmStatic
        private fun evaluateJavascript(
            webView: WebView,
            javascriptString: String,
            resultCallback: ValueCallback<String>?
        ) {
            var jsString = javascriptString
            if (jsString.startsWith(SCHEME_JAVASCRIPT)) {
                jsString = jsString.substring(SCHEME_JAVASCRIPT.length)
            }
            jsString = jsString.replace("\t".toRegex(), "    ")

            // Android 4.4 (KitKat, 19) or higher
            webView.evaluateJavascript(jsString) { value: String ->
                Timber.d("onReceiveValue: $value")
                var tmp = value
                if (tmp.startsWith("\"")) {
                    tmp = tmp.substring(1)
                }
                if (tmp.endsWith("\"")) {
                    tmp = tmp.substring(0, tmp.length - 1)
                }
                resultCallback?.onReceiveValue(tmp)
            }
        }
    }
}