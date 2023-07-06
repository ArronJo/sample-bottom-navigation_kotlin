package com.snc.initializer.timber

import android.content.Context
import androidx.startup.Initializer
import timber.log.Timber

class TimberInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        Timber.plant(Timber.DebugTree())
        Timber.i("[LifeCycle] TimberInitializer is started.")
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}