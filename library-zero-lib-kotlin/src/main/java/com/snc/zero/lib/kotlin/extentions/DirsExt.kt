package com.snc.zero.lib.kotlin.extentions

import android.content.Context
import java.io.File

fun Context.getInternalFilesDir(path: String): File {
    return File(filesDir, path)
}
