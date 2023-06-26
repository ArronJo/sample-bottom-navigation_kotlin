package com.snc.zero.ui.kotlin.extentions

import android.os.Handler
import android.os.Looper

val globalHandler: Handler = Handler(Looper.getMainLooper())

fun postDelayed(runnable: Runnable, delayMillis: Long) {
    globalHandler.postDelayed(runnable, delayMillis)
}
