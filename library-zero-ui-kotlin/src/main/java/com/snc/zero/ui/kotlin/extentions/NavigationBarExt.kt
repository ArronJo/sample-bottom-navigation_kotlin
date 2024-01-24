package com.snc.zero.ui.kotlin.extentions

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import android.view.*

fun Window.setLightNavigationBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        @Suppress("DEPRECATION")
        var flag = decorView.systemUiVisibility
        @Suppress("DEPRECATION")
        flag = flag or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = flag
    }
}

fun Window.clearLightNavigationBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        insetsController?.setSystemBarsAppearance(
            0,
            WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        @Suppress("DEPRECATION")
        var flag = decorView.systemUiVisibility
        @Suppress("DEPRECATION")
        flag = flag and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = flag
    }
}

@SuppressLint("InternalInsetResource", "DiscouragedApi")
fun Resources.getNavigationBarHeight(): Int {
    if (!useSoftNavigationBar()) {
        return 0
    }
    var bottomBarHeight = 0
    val resourceIdBottom = getIdentifier("navigation_bar_height", "dimen", "android")
    if (resourceIdBottom > 0) { bottomBarHeight = getDimensionPixelSize(resourceIdBottom) }
    //if (bottomBarHeight < 48) {
    //    return 0
    //}
    return bottomBarHeight
}

@SuppressLint("DiscouragedApi")
fun Resources.useSoftNavigationBar(): Boolean {
    val resourceId = getIdentifier("config_showNavigationBar", "bool", "android")
    if (resourceId > 0) {
        return getBoolean(resourceId)
    }
    return false
}
