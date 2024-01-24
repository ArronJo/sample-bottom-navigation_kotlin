package com.snc.zero.ui.kotlin.extentions

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsetsController

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