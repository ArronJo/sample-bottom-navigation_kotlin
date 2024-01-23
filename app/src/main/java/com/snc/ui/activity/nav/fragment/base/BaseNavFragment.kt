package com.snc.ui.activity.nav.fragment.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.transition.TransitionInflater
import com.snc.consts.AppConfig
import com.snc.sample.bottom_navigation_kotlin.R
import com.snc.zero.ui.kotlin.extentions.getStatusBarHeight
import com.snc.zero.ui.kotlin.fragment.base.BaseFragment
import timber.log.Timber

abstract class BaseNavFragment(contentLayoutId: Int = 0) : BaseFragment(contentLayoutId) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(com.snc.zero.resources.R.transition.nothing)
        enterTransition = inflater.inflateTransition(com.snc.zero.resources.R.transition.nothing)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (AppConfig.FEATURE_FULLSCREEN) {
            insetsFullScreen(view)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun insetsFullScreen(view: View) {
        //val fitLayout = view.findViewById<ViewGroup>(R.id.fitLayout)
        //ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        //    var paddingBottom: Int = insets.getNavigationBarHeight()
        //    fitLayout?.let {
        //        if (!ViewCompat.getFitsSystemWindows(it)) {
        //            paddingBottom = insets.getNavigationBarImeHeight()
        //        }
        //    }
        //    Timber.i("BaseNavFragment::fragment - setPadding(${v.paddingLeft}, 0, ${v.paddingRight}, ${paddingBottom})")
        //    v.setPadding(v.paddingLeft, 0, v.paddingRight, paddingBottom)
        //    return@setOnApplyWindowInsetsListener insets
        //}

        val contentLayout = view.findViewById<ViewGroup>(R.id.contentLayout)
        contentLayout?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val statusBarHeight = insets.getStatusBarHeight()
                Timber.i("BaseNavFragment::contentLayout - setPadding(${v.paddingLeft}, 0, ${v.paddingRight}, ${v.paddingBottom})")
                v.setPadding(v.paddingLeft, statusBarHeight, v.paddingRight, v.paddingBottom)
                return@setOnApplyWindowInsetsListener insets
            }
        }
    }
}