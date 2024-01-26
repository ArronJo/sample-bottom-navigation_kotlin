package com.snc.zero.lib.kotlin.backpressed

import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import com.snc.zero.lib.kotlin.backpressed.listener.OnBackPressedCallbackListener

object BackPressedCallbackCompat {

    const val TIME_INTERVAL_BACK_KEY_TWICE = 2000L

    private var backKeyPressedTime = 0L

    fun addCallback(activity: ComponentActivity, callback: OnBackPressedCallbackListener) {
        activity.onBackPressedDispatcher.addCallback(activity, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() > backKeyPressedTime + TIME_INTERVAL_BACK_KEY_TWICE) {
                    backKeyPressedTime = System.currentTimeMillis()
                    callback.handleOnBackPressed(false)
                    return
                }

                if (isEnabled) {
                    callback.handleOnBackPressed(true)
                } else {
                    activity.onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }
}