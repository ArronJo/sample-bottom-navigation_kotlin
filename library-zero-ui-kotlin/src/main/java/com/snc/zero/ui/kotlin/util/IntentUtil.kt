package com.snc.zero.ui.kotlin.util

import android.app.Activity
import android.content.DialogInterface
import com.snc.zero.lib.kotlin.util.IntentSettings
import com.snc.zero.ui.kotlin.dialog.ModalBuilder

class IntentUtil {

    companion object {

        @JvmStatic
        fun showGotoSettingsDialog(
            activity: Activity,
            packageName: String = activity.packageName,
            msg: List<String>? = null
        ) {
            var message =
                activity.resources.getString(com.snc.zero.resources.R.string.msg_please_allow_all_required_permissions)
            msg?.let {
                message += "\n${it.joinToString(separator = ",")}"
            }

            ModalBuilder.with(activity)
                .setTitle(com.snc.zero.resources.R.string.notice)
                .setMessage(message)
                .setPositiveButton(com.snc.zero.resources.R.string.settings) { _: DialogInterface?, _: Int ->
                    IntentSettings.manageAppSettings(activity, packageName)
                }
                .setNegativeButton(com.snc.zero.resources.R.string.cancel) { _, _ -> }
                .show()
        }
    }
}