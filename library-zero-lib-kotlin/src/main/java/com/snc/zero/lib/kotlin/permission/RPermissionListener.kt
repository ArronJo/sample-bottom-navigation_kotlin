package com.snc.zero.lib.kotlin.permission

interface RPermissionListener {

    fun onPermissionGranted(grantPermissions: List<String>)

    fun onPermissionDenied(deniedPermissions: List<String>)

    fun onPermissionRationaleShouldBeShown(deniedPermissions: List<String>)

}