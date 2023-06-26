package com.snc.zero.ui.kotlin.fragment.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.snc.zero.ui.kotlin.extentions.setDarkThemeSystemBar

abstract class BaseFragment(contentLayoutId: Int = 0) : Fragment(contentLayoutId) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDarkThemeSystemBar()
    }
}