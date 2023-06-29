package com.snc.zero.ui.kotlin.util

import android.content.Context
import android.content.DialogInterface
import com.snc.zero.lib.kotlin.util.IntentSettings
import com.snc.zero.ui.kotlin.dialog.ModalBuilder

class IntentUtil {

    companion object {

        @JvmStatic
        fun showGotoSettingsDialog(context: Context, packageName: String = context.packageName, msg: List<String>? = null) {
            var message = context.resources.getString(com.snc.zero.resources.R.string.msg_please_allow_all_required_permissions)
            msg?.let {
                message += "\n${it.joinToString(separator = ",")}"
            }

            ModalBuilder.with(context)?.let {
                it.setTitle(com.snc.zero.resources.R.string.notice)
                it.setMessage(message)
                it.setPositiveButton(com.snc.zero.resources.R.string.settings) { _: DialogInterface?, _: Int ->
                    IntentSettings.manageAppSettings(context, packageName)
                }
                it.setNegativeButton(com.snc.zero.resources.R.string.cancel) { _, _ -> }
                it.show()
            }
        }
    }
}