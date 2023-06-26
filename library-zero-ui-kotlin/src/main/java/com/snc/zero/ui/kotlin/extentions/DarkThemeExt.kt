package com.snc.zero.ui.kotlin.extentions

import android.content.res.Configuration
import android.content.res.Resources
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

fun Fragment.setDarkThemeSystemBar() {
    if (resources.isDarkMode()) {
        activity?.window?.clearLightStatusBar()
        activity?.window?.clearLightNavigationBar()
    } else {
        activity?.window?.setLightStatusBar()
        activity?.window?.setLightNavigationBar()
    }
}

fun DialogFragment.setDarkThemeSystemBar() {
    if (resources.isDarkMode()) {
        dialog?.window?.clearLightStatusBar()
        dialog?.window?.clearLightNavigationBar()
    } else {
        dialog?.window?.setLightStatusBar()
        dialog?.window?.setLightNavigationBar()
    }
}


@Suppress("BooleanMethodIsAlwaysInverted")
fun Resources.isDarkMode(): Boolean {
    return when (configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> true
        Configuration.UI_MODE_NIGHT_NO -> false
        Configuration.UI_MODE_NIGHT_UNDEFINED -> false
        else -> false
    }
}