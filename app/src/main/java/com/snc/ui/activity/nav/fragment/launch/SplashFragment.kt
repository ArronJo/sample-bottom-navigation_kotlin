package com.snc.ui.activity.nav.fragment.launch

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.snc.ui.activity.nav.bizconst.BizConst
import com.snc.sample.bottom_navigation_kotlin.R
import com.snc.zero.ui.kotlin.extentions.navigateWithDefaultAnimation
import com.snc.zero.ui.kotlin.extentions.postDelayed
import com.snc.zero.ui.kotlin.fragment.base.BaseFragment
import timber.log.Timber

class SplashFragment : BaseFragment(R.layout.fragment_nav_splash) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val params = arguments?.getString(BizConst.ARGUMENT_KEY_PARAM, "")
        Timber.i("SplashFragment:: arguments : ${BizConst.ARGUMENT_KEY_PARAM} = $params")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val imgView = view.findViewById<ImageView>(R.id.imgView)
        imgView?.let {
            if (it.background is AnimationDrawable) {
                val animationDrawable = it.background as AnimationDrawable
                animationDrawable.start()
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        postDelayed({
            goToNext()
        }, 1500)
    }

    private fun goToNext() {
        findNavController().navigateWithDefaultAnimation(
            R.id.menu_my_contract,
            //R.id.menu_find_product,
            //R.id.menu_full_menu,
            bundleOf("arg1" to "1"),
            NavOptions.Builder()
        )
    }
}