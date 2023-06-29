package com.snc.zero.lib.kotlin.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

class IntentSettings {

    companion object {

        @JvmStatic
        fun manageAppSettings(
            context: Context,
            packageName: String = context.packageName,
        ) {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            )

            context.startActivity(intent)
        }
    }
}