package com.snc.zero.lib.kotlin.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import timber.log.Timber

class PackageUtil {

    companion object {

        @SuppressLint("QueryPermissionsNeeded")
        @JvmStatic
        fun getInstalledPackageInfo(
            context: Context,
            includeSysPackages: Boolean
        ): ArrayList<PackageInfo> {
            val result: ArrayList<PackageInfo> = ArrayList()
            val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
            } else {
                context.packageManager.getInstalledPackages(0)
            }
            for (packageInfo in packages) {
                Timber.d("Installed package :" + packageInfo.packageName)
                val isSystemPackage =
                    packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
                if (!includeSysPackages && !isSystemPackage && null == packageInfo.versionName) {
                    continue
                }
                result.add(packageInfo)
            }
            return result
        }
    }
}