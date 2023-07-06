package com.snc.application

import android.content.Context
import androidx.multidex.MultiDexApplication
import timber.log.Timber

class AppApplication : MultiDexApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }
}