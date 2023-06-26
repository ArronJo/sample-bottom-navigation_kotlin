package com.snc.zero.ui.kotlin.dialog.base

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.WindowManager.BadTokenException
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.snc.zero.ui.kotlin.R
import timber.log.Timber

abstract class BaseDialog(
    val activity: Activity,
    themeResId: Int = R.style.LibTheme_Dialog_Transparent
) : Dialog(activity, themeResId) {

    private var isAttachedWindow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        setCancelable(false)
        setCanceledOnTouchOutside(false)

        window?.let {
            it.clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
            )
            @Suppress("DEPRECATION")
            it.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
            )
        }

        initializeSetup()
    }

    override fun onStart() {
        super.onStart()

        val contentView = window?.decorView?.findViewById<ViewGroup>(android.R.id.content)
        contentView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { view, insets ->
                val types = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()

                val typeInsets = insets.getInsets(types)

                Timber.i("android.R.id.content::setPadding(${typeInsets.left}, ${typeInsets.top}, ${typeInsets.right}, ${typeInsets.bottom})")
                view.setPadding(
                    typeInsets.left,
                    typeInsets.top,
                    typeInsets.right,
                    typeInsets.bottom
                )

                return@setOnApplyWindowInsetsListener WindowInsetsCompat.CONSUMED
            }
        }
    }

    private fun initializeSetup() {
        window?.let {
            it.clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
            )
            @Suppress("DEPRECATION")
            it.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
            )
        }
    }

    override fun onAttachedToWindow() {
        isAttachedWindow = true
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        isAttachedWindow = false
        super.onDetachedFromWindow()
    }

    open fun isAttachedWindow(): Boolean {
        return isAttachedWindow
    }

    override fun show() {
        try {
            val activity = activity
            if (activity.isFinishing) {
                Timber.e("can not show dialog")
                return
            }
            super.show()
        } catch (e: BadTokenException) {
            Timber.e(e, "BadTokenException")
        } catch (e: IllegalArgumentException) {
            Timber.e(e, "IllegalArgumentException")
        } catch (e: Exception) {
            Timber.e(e, "Exception")
        } catch (t: Throwable) {
            Timber.e(t, "Throwable")
        }
    }

    override fun dismiss() {
        try {
            if (isAttachedWindow()) {
                super.dismiss()
            }
        } catch (e: BadTokenException) {
            Timber.e(e, "BadTokenException")
        } catch (e: java.lang.IllegalArgumentException) {
            Timber.e(e, "IllegalArgumentException")
        } catch (e: java.lang.Exception) {
            Timber.e(e, "Exception")
        }
    }
}