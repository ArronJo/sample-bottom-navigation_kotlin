package com.snc.ui.activity.nav.fragment.biz

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.snc.configs.AppConfig
import com.snc.sample.bottom_navigation_kotlin.R
import com.snc.ui.activity.nav.bizconst.BizConst
import com.snc.ui.activity.nav.bizconst.BizConst.Companion.KEY_BIZ_WEB_FRAGMENT
import com.snc.ui.activity.nav.bizconst.BizConst.Companion.KEY_BIZ_WEB_FRAGMENT_RESULT
import com.snc.ui.activity.webview.bridge.AndroidBridge
import com.snc.ui.activity.webview.chooser.BizFileChooserListener
import com.snc.ui.activity.webview.chooser.listener.FileChooserListener
import com.snc.utils.extenstions.loadUrlWithHeader
import com.snc.utils.extenstions.setup
import com.snc.zero.ui.kotlin.extentions.getNavigationBarHeight
import com.snc.zero.ui.kotlin.extentions.getStatusBarHeight
import com.snc.zero.ui.kotlin.extentions.popup
import com.snc.zero.ui.kotlin.extentions.setDarkThemeSystemBar
import com.snc.zero.ui.kotlin.fragment.base.BaseDialogFragment
import io.github.muddz.styleabletoast.StyleableToast
import timber.log.Timber

class BizWebFragment : BaseDialogFragment() {

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
            inflater.inflate(R.layout.fragment_biz_webview, container, false)

        if (AppConfig.FEATURE_FULLSCREEN) {
            dialog?.window?.let { window ->
                val layout = view.findViewById<ViewGroup>(R.id.contentLayout)
                layout?.let {
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    ViewCompat.setOnApplyWindowInsetsListener(layout) { v, insets ->
                        val statusBarHeight = insets.getStatusBarHeight()
                        val navigationBarHeight = insets.getNavigationBarHeight()
                        Timber.i("BizAppWebFragment::setPadding(${v.paddingLeft}, ${statusBarHeight}, ${v.paddingRight}, ${navigationBarHeight})")
                        v.setPadding(
                            v.paddingLeft,
                            statusBarHeight,
                            v.paddingRight,
                            navigationBarHeight
                        )
                        return@setOnApplyWindowInsetsListener insets
                    }
                }
            }
        }

        val navBack = view.findViewById<ImageButton>(R.id.nav_back)
        navBack?.let {
            it.setOnClickListener {
                dismiss()

                setFragmentResult(
                    KEY_BIZ_WEB_FRAGMENT,
                    bundleOf(KEY_BIZ_WEB_FRAGMENT_RESULT to "dismiss")
                )
            }
        }

        val moreBtn1 = view.findViewById<ImageButton>(R.id.more_btn_1)
        moreBtn1?.let {
            it.setOnClickListener {
                //Toast.makeText(view.context, "clicked more_btn_1.", Toast.LENGTH_SHORT).show()
                StyleableToast.Builder(view.context)
                    .text("clicked more_btn_1.")
                    .textColor(Color.WHITE)
                    .backgroundColor(ContextCompat.getColor(view.context, com.snc.zero.resources.R.color.positive))
                    .gravity(Gravity.TOP)
                    .iconEnd(com.snc.zero.resources.R.drawable.ic_phone)
                    .cornerRadius(16)
                    .show()
            }
        }
        val moreBtn2 = view.findViewById<ImageButton>(R.id.more_btn_2)
        moreBtn2?.let {
            it.setOnClickListener {
                //Toast.makeText(view.context, "clicked more_btn_2.", Toast.LENGTH_SHORT).show()
                StyleableToast.Builder(view.context)
                    .text("clicked more_btn_2.")
                    .textColor(Color.WHITE)
                    .backgroundColor(ContextCompat.getColor(view.context, com.snc.zero.resources.R.color.negative))
                    .gravity(Gravity.BOTTOM)
                    .iconStart(com.snc.zero.resources.R.drawable.ic_bell)
                    .cornerRadius(8)
                    .show()
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