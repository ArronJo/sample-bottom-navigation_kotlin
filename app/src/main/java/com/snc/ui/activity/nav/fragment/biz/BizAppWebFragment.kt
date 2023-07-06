package com.snc.ui.activity.nav.fragment.biz

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.findNavController
import com.snc.ui.activity.nav.bizconst.BizConst
import com.snc.ui.activity.webview.bridge.AndroidBridge
import com.snc.ui.activity.webview.chooser.BizFileChooserListener
import com.snc.ui.activity.webview.chooser.listener.FileChooserListener
import com.snc.utils.extenstions.loadUrlWithHeader
import com.snc.utils.extenstions.setup
import com.snc.sample.bottom_navigation_kotlin.R
import com.snc.zero.ui.kotlin.extentions.getNavigationBarImeHeight
import com.snc.zero.ui.kotlin.extentions.getStatusBarHeight
import com.snc.zero.ui.kotlin.extentions.popup
import com.snc.zero.ui.kotlin.extentions.setDarkThemeSystemBar
import com.snc.zero.ui.kotlin.fragment.base.BaseDialogFragment
import timber.log.Timber

class BizAppWebFragment : BaseDialogFragment() {

    private val fileChooserActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            fileChooserListener?.onActivityResultFileChooser(result)
        }

    private var fileChooserListener: FileChooserListener? = null

    private var bizWeb: WebView? = null

    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(
            STYLE_NORMAL,
            com.snc.zero.ui.kotlin.R.style.LibTheme_Dialog_Fullscreen_Transparent
        )

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

        fileChooserListener = BizFileChooserListener(fileChooserActivityResultLauncher)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_nav_biz_app_webview, container, false)

        dialog?.window?.let { window ->
            val layout = view.findViewById<ViewGroup>(R.id.contentLayout)
            layout?.let {
                WindowCompat.setDecorFitsSystemWindows(window, false)
                ViewCompat.setOnApplyWindowInsetsListener(layout) { v, insets ->
                    val paddingTop = insets.getStatusBarHeight()
                    val paddingBottom = insets.getNavigationBarImeHeight()
                    Timber.i("BizAppWebFragment::setPadding(${v.paddingLeft}, ${paddingTop}, ${v.paddingRight}, ${paddingBottom})")
                    v.setPadding(
                        v.paddingLeft,
                        paddingTop,
                        v.paddingRight,
                        paddingBottom
                    )
                    return@setOnApplyWindowInsetsListener insets
                }
            }
        }

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

        activity?.let {
            startBizWeb(it, view)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    override fun onDestroyView() {
        bizWeb?.destroy()
        super.onDestroyView()
    }

    private fun startBizWeb(activity: Activity, view: View) {
        val progressBar = view.findViewById<ProgressBar>(R.id.progressbar)
        bizWeb = view.findViewById(R.id.bizWeb)

        bizWeb?.let { web ->
            web.addJavascriptInterface(
                AndroidBridge(web, navController = findNavController()),
                "AndroidBridge"
            )

            web.setup(
                activity = activity,
                progressBar = progressBar,
                fileChooserListener = fileChooserListener
            )

            url?.let { url ->
                web.loadUrlWithHeader(activity, url)
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