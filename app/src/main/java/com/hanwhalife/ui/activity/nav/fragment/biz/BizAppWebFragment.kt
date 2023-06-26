package com.hanwhalife.ui.activity.nav.fragment.biz

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.hanwhalife.ui.activity.nav.bizconst.BizConst
import com.hanwhalife.ui.activity.webview.bridge.AndroidBridge
import com.hanwhalife.utils.extenstions.loadUrlWithHeader
import com.hanwhalife.utils.extenstions.setup
import com.snc.sample.bottom_navigation_kotlin.R
import com.snc.zero.ui.kotlin.extentions.popup
import com.snc.zero.ui.kotlin.extentions.setDarkThemeSystemBar
import com.snc.zero.ui.kotlin.fragment.base.BaseDialogFragment
import timber.log.Timber

class BizAppWebFragment : BaseDialogFragment() {

    private var bizWeb: WebView? = null

    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, com.snc.zero.ui.kotlin.R.style.LibTheme_Dialog_Fullscreen_Transparent)

        val onBackPressedDispatcher = requireActivity().onBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Timber.i("BizAppWebFragment:: onBackPressed : handleOnBackPressed() : isEnabled = $isEnabled")
                if (canGoBack()) {
                    return
                }

                if (isEnabled) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                } else {
                    onBackPressedDispatcher.onBackPressed()
                }

                findNavController().popup()
            }
        })

        url = arguments?.getString(BizConst.ARGUMENT_KEY_URL, "")
        Timber.i("BizAppWebFragment:: arguments : ${BizConst.ARGUMENT_KEY_URL} = $url")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_nav_biz_app_webview, container, false)

        val navBack = view.findViewById<ImageButton>(R.id.nav_back)
        navBack?.let {
            it.setOnClickListener {
                dismiss()
            }
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDarkThemeSystemBar()

        startBizWeb(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        bizWeb?.destroy()
    }

    private fun startBizWeb(view: View) {
        val progressBar = view.findViewById<ProgressBar>(R.id.progressbar)
        bizWeb = view.findViewById(R.id.bizWeb)

        bizWeb?.let { web ->
            web.addJavascriptInterface(AndroidBridge(web, navController = findNavController()), "AndroidBridge")
            web.setup(progressBar = progressBar)

            url?.let { url ->
                web.loadUrlWithHeader(url)
            }
        }
    }

    @Suppress("SameReturnValue")
    private fun canGoBack(): Boolean {
        bizWeb?.let {
            if (it.canGoBack()) {
                it.goBack()
                return true
            }
        }
        return false
    }
}