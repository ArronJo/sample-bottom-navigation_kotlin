package com.snc.ui.activity.nav.fragment.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.transition.TransitionInflater
import com.snc.configs.AppConfig
import com.snc.sample.bottom_navigation_kotlin.R
import com.snc.zero.ui.kotlin.extentions.getImeHeight
import com.snc.zero.ui.kotlin.extentions.getNavigationBarImeHeight
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
        val fitLayout = view.findViewById<ViewGroup>(R.id.fitLayout)
        fitLayout?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                if (insets.getImeHeight() > 0) {
                    Timber.i("BaseNavFragment::fitLayout - setPadding + Ime(${v.paddingLeft}, 0, ${v.paddingRight}, ${insets.getNavigationBarImeHeight()})")
                    v.setPadding(v.paddingLeft, 0, v.paddingRight, insets.getNavigationBarImeHeight())
                } else {
                    Timber.i("BaseNavFragment::fitLayout - setPadding + Normal(${v.paddingLeft}, 0, ${v.paddingRight}, 0)")
                    v.setPadding(v.paddingLeft, 0, v.paddingRight, 0)
                }
                return@setOnApplyWindowInsetsListener insets
            }
        }

        val contentLayout = view.findViewById<ViewGroup>(R.id.contentLayout)
        contentLayout?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val statusBarHeight = insets.getStatusBarHeight()
                Timber.i("BaseNavFragment::contentLayout - setPadding(${v.paddingLeft}, ${statusBarHeight}, ${v.paddingRight}, ${v.paddingBottom})")
                v.setPadding(v.paddingLeft, statusBarHeight, v.paddingRight, v.paddingBottom)
                return@setOnApplyWindowInsetsListener insets
            }
        }
    }
}