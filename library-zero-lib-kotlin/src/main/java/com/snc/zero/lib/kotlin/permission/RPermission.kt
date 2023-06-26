package com.snc.zero.lib.kotlin.permission

import android.app.Activity
import android.content.Context
import android.content.res.Resources.NotFoundException
import android.text.TextUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import timber.log.Timber

class RPermission(private val context: Context) {

    companion object {

        @JvmStatic
        fun with(context: Context): RPermission {
            return RPermission(context)
        }

        @JvmStatic
        fun isGranted(context: Context?, permissions: List<String>): Boolean {
            for (permission in permissions) {
                if (isGranted(context, permission)) {
                    return false
                }
            }
            return true
        }

        @JvmStatic
        fun isGranted(context: Context?, permission: String): Boolean {
            return PermissionChecker.PERMISSION_GRANTED == PermissionChecker.checkSelfPermission(
                context!!,
                permission
            )
        }
    }

    private var rationalTitle: String? = null
    private var rationalMessage: String? = null
    private lateinit var permissions: List<String>
    private var listener: RPermissionListener? = null

    fun setRationalTitle(rationalTitle: String): RPermission {
        this.rationalTitle = rationalTitle
        return this
    }

    fun setRationalTitle(resId: Int): RPermission {
        rationalTitle = getString(context, resId)
        return this
    }

    fun setRationalMessage(rationalMessage: String): RPermission {
        this.rationalMessage = rationalMessage
        return this
    }

    fun setRationalMessage(resId: Int): RPermission {
        rationalMessage = getString(context, resId)
        return this
    }

    fun setPermissionListener(listener: RPermissionListener?): RPermission {
        this.listener = listener
        return this
    }

    fun setPermissions(permissions: ArrayList<String>): RPermission {
        this.permissions = permissions
        return this
    }

    fun setPermissions(vararg permissions: String): RPermission {
        this.permissions = permissions.toList()
        return this
    }

    fun check() {
        if (permissions.isEmpty()) {
            listener?.onPermissionGranted(permissions.toMutableList())
            return
        }

        val builder = TedPermission.create()
        builder.setPermissions(*permissions.toTypedArray())
        if (!TextUtils.isEmpty(rationalMessage)) {
            if (!TextUtils.isEmpty(rationalTitle)) {
                builder.setRationaleTitle(rationalTitle)
            }
            builder.setRationaleMessage(rationalMessage)
        }
        builder.setPermissionListener(object : PermissionListener {
            override fun onPermissionGranted() {
                listener?.onPermissionGranted(permissions.toMutableList())
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                listener?.let {
                    if (!shouldShowRequestPermissionRationale(deniedPermissions)) {
                        it.onPermissionRationaleShouldBeShown(deniedPermissions)
                    } else {
                        it.onPermissionDenied(deniedPermissions)
                    }
                }
            }
        })
        builder.check()
    }

    fun shouldShowRequestPermissionRationale(needPermissions: List<String>?): Boolean {
        if (null == needPermissions) {
            return false
        }
        for (permission in needPermissions) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    (context as Activity?)!!,
                    permission
                )
            ) {
                return false
            }
        }
        return true
    }

    private fun getString(context: Context?, resId: Int): String {
        if (null != context) {
            try {
                return context.getString(resId)
            } catch (e: NotFoundException) {
                Timber.e(Log.getStackTraceString(e))
            }
        }
        return ""
    }
}