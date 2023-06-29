package com.snc.zero.lib.kotlin.util

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

class UriUtil {

    companion object {

        @Throws(PackageManager.NameNotFoundException::class)
        fun fromFile(context: Context, file: File): Uri {
            val newUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val packageName = context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_META_DATA
                ).packageName

                FileProvider.getUriForFile(
                    context, "$packageName.fileprovider",
                    file
                )
            } else {
                Uri.fromFile(file)
            }
            return newUri
        }
    }
}