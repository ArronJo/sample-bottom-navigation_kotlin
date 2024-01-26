package com.snc.ui.activity.nav.fragment.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.transition.TransitionInflater
import com.snc.configs.AppConfig
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
            setupFullScreen(view)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupFullScreen(view: View) {
        val contentLayout = view.findViewById<ViewGroup>(R.id.contentLayout)
        contentLayout?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val statusBarHeight = insets.getStatusBarHeight()
                Timber.i("BaseNavFragment::setPadding(${v.paddingLeft}, ${statusBarHeight}, ${v.paddingRight}, ${v.paddingBottom})")
                it.setPadding(v.paddingLeft, statusBarHeight, v.paddingRight, v.paddingBottom)
                return@setOnApplyWindowInsetsListener insets
            }
        }
    }
}