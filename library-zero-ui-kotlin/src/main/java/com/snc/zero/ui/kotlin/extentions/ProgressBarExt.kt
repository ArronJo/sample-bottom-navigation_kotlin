package com.snc.zero.ui.kotlin.extentions

import android.os.Build
import android.widget.ProgressBar

fun ProgressBar.setProgressWithAnim(value: Int): ProgressBar {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        setProgress(value, true)
    } else {
        progress = value
    }
    return this
}
