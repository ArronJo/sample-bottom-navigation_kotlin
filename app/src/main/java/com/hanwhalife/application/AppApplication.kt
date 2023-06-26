package com.hanwhalife.application

import android.content.Context
import androidx.multidex.MultiDexApplication
import timber.log.Timber

class AppApplication : MultiDexApplication() {

    override fun attachBaseContext(base: Context?) {
        Timber.plant(Timber.DebugTree())
        super.attachBaseContext(base)
    }
}