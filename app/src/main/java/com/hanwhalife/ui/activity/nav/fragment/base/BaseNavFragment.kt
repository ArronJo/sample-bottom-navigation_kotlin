package com.hanwhalife.ui.activity.nav.fragment.base

import android.os.Bundle
import androidx.transition.TransitionInflater
import com.snc.zero.ui.kotlin.fragment.base.BaseFragment

abstract class BaseNavFragment(contentLayoutId: Int = 0) : BaseFragment(contentLayoutId) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(com.snc.zero.resources.R.transition.nothing)
        enterTransition = inflater.inflateTransition(com.snc.zero.resources.R.transition.nothing)
    }
}