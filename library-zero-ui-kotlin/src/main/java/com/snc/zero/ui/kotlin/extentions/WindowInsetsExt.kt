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

fun WindowInsetsCompat.getNavigationBarHeight(): Int {
    val navigationHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars()).bottom
    } else {
        @Suppress("DEPRECATION")
        systemWindowInsetBottom
    }
    return navigationHeight
}

fun WindowInsetsCompat.getImeHeight(): Int {
    return getInsets(WindowInsetsCompat.Type.ime()).bottom
}

fun WindowInsetsCompat.getNavigationBarImeHeight(): Int {
    val navigationBarHeight = getNavigationBarHeight()
    val imeHeight = getImeHeight()
    return if (imeHeight > 0) imeHeight else navigationBarHeight
}
