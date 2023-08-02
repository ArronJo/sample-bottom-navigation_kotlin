package com.snc.application

import android.content.Context
import androidx.multidex.MultiDexApplication

class AppApplication : MultiDexApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }
}