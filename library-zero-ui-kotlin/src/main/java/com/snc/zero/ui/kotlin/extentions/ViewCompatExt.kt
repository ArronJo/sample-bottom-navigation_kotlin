package com.snc.zero.ui.kotlin.extentions

import android.view.View
import androidx.core.view.ViewCompat
import timber.log.Timber

fun View.setOnApplyWindowInsets(
    fitsSystemWindows: Boolean = false
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val paddingTop = if (fitsSystemWindows) {
            insets.getStatusBarHeight()
        }
        else {
            v.paddingTop
        }
        val paddingLeft = v.paddingLeft
        val paddingRight = v.paddingRight
        val paddingBottom = v.paddingBottom

        Timber.i("${this.javaClass}::setPadding($paddingLeft, $paddingTop, $paddingRight, $paddingBottom)")
        v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)

        return@setOnApplyWindowInsetsListener insets
    }
}
