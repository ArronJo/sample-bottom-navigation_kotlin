package com.snc.zero.lib.kotlin.mimetype

import android.webkit.MimeTypeMap

class MimeTypes {

    companion object {

		@JvmStatic
        fun getMimeTypeFromFileName(fileName: String): String? {
            val extension = MimeTypeMap.getFileExtensionFromUrl(fileName)
            return getMimeTypeFromExtension(extension)
        }

		@JvmStatic
        fun getMimeTypeFromExtension(extension: String): String? {
            val map = MimeTypeMap.getSingleton()
            return map.getMimeTypeFromExtension(extension)
        }
    }
}