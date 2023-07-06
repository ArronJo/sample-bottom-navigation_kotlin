package com.snc.initializer

import android.content.Context
import androidx.startup.Initializer
import com.snc.initializer.timber.TimberInitializer
import timber.log.Timber

class WorkManagerInitializer: Initializer<Unit> {

    override fun create(context: Context) {
        Timber.i("[LifeCycle] WorkManagerInitializer is started.")
        return
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(
            TimberInitializer::class.java,
        )
    }
}