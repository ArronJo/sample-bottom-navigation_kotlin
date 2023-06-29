package com.snc.zero.lib.kotlin.extentions

import android.content.Context
import android.os.Environment
import java.io.File

fun Context.getInternalFilesDir(path: String): File {
    return File(filesDir, path)
}

fun Context.getExternalFilesDir(): File? {
    return getExternalFilesDir(null)
}

fun Context.getMediaDir(type: String): File? {
    val storageDir: File? = getExternalFilesDir()
    if ("image".equals(type, ignoreCase = true) || "video".equals(type, ignoreCase = true)) {
        return File(File(storageDir, Environment.DIRECTORY_DCIM), "Camera")
    } else if ("audio".equals(type, ignoreCase = true)) {
        return File(storageDir, "Voice Recorder")
    }
    return null
}
