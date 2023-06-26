package com.snc.zero.ui.kotlin.extentions

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavOptions

fun NavController.navigateWithDefaultAnimation(@IdRes resId: Int, args: Bundle? = null, navOptionsBuilder: NavOptions.Builder? = null) {
    val builder = navOptionsBuilder ?: NavOptions.Builder()

    val navOptions: NavOptions = builder
        .setEnterAnim(androidx.navigation.ui.R.anim.nav_default_enter_anim)
        .setExitAnim(androidx.navigation.ui.R.anim.nav_default_exit_anim)
        .setPopEnterAnim(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim)
        .setPopExitAnim(androidx.navigation.ui.R.anim.nav_default_pop_exit_anim)
        .build()

    navigate(resId = resId, args = args, navOptions = navOptions)
}

fun NavController.popup() {
    popBackStack()
}
