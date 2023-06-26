package com.snc.zero.ui.kotlin.extentions

import android.os.Build
import androidx.core.view.WindowInsetsCompat

fun WindowInsetsCompat.getStatusBarHeight(): Int {
    val statusHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars()).top
    } else {
        @Suppress("DEPRECATION")
        systemWindowInsetTop
    }
    return statusHeight
}
