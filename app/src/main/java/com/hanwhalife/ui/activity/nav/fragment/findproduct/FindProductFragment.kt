package com.hanwhalife.ui.activity.nav.fragment.findproduct

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.hanwhalife.ui.activity.nav.bizconst.BizConst
import com.hanwhalife.ui.activity.nav.fragment.base.BaseNavFragment
import com.snc.sample.bottom_navigation_kotlin.R
import timber.log.Timber

class FindProductFragment : BaseNavFragment(R.layout.fragment_nav_find_product) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackPressedDispatcher = requireActivity().onBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Timber.i("FindProductFragment:: onBackPressed : handleOnBackPressed() : isEnabled = $isEnabled")
                if (isEnabled) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                } else {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        val params = arguments?.getString(BizConst.ARGUMENT_KEY_PARAM, "")
        Timber.i("FindProductFragment:: arguments : ${BizConst.ARGUMENT_KEY_PARAM} = $params")
    }
}