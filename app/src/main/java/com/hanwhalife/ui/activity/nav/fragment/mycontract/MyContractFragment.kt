package com.hanwhalife.ui.activity.nav.fragment.mycontract

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import com.hanwhalife.ui.activity.nav.NavActivity
import com.hanwhalife.ui.activity.nav.bizconst.BizConst
import com.hanwhalife.ui.activity.nav.fragment.base.BaseNavFragment
import com.snc.sample.bottom_navigation_kotlin.R
import timber.log.Timber

class MyContractFragment : BaseNavFragment(R.layout.fragment_nav_my_contract) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackPressedDispatcher = requireActivity().onBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Timber.i("MyContractFragment:: onBackPressed : handleOnBackPressed() : isEnabled = $isEnabled")
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
        Timber.i("MyContractFragment:: arguments : ${BizConst.ARGUMENT_KEY_PARAM} = $params")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setup(view)
    }

    @SuppressLint("SetTextI18n")
    private fun setup(view: View) {
        val requireItem1 = view.findViewById<LinearLayout>(R.id.requireItem1)
        requireItem1?.let {
            val iv = it.findViewById<ImageView>(R.id.img)
            iv?.setImageResource(com.snc.zero.resources.R.drawable.ic_save_folder)
            val text1 = it.findViewById<TextView>(R.id.text1)
            text1?.text = "blah blah (require)"
            val text2 = it.findViewById<TextView>(R.id.text2)
            text2?.text = "blah blah blah blah blah blah"
        }

        val requireItem2 = view.findViewById<LinearLayout>(R.id.requireItem2)
        requireItem2?.let {
            val iv = it.findViewById<ImageView>(R.id.img)
            iv?.setImageResource(com.snc.zero.resources.R.drawable.ic_phone)
            val text1 = it.findViewById<TextView>(R.id.text1)
            text1?.text = "blah blah (require)"
            val text2 = it.findViewById<TextView>(R.id.text2)
            text2?.text = "blah blah blah blah blah blah"
        }

        val optionalItem1 = view.findViewById<LinearLayout>(R.id.optionalItem1)
        optionalItem1?.let {
            val iv = it.findViewById<ImageView>(R.id.img)
            iv?.setImageResource(com.snc.zero.resources.R.drawable.ic_bell)
            val text1 = it.findViewById<TextView>(R.id.text1)
            text1?.text = "blah blah (optional)"
            val text2 = it.findViewById<TextView>(R.id.text2)
            text2?.text = "blah blah blah blah blah blah"
        }

        val optionalItem2 = view.findViewById<LinearLayout>(R.id.optionalItem2)
        optionalItem2?.let {
            val iv = it.findViewById<ImageView>(R.id.img)
            iv?.setImageResource(com.snc.zero.resources.R.drawable.ic_location)
            val text1 = it.findViewById<TextView>(R.id.text1)
            text1?.text = "blah blah (optional)"
            val text2 = it.findViewById<TextView>(R.id.text2)
            text2?.text = "blah blah blah blah blah blah"
        }

        val optionalItem3 = view.findViewById<LinearLayout>(R.id.optionalItem3)
        optionalItem3?.let {
            val iv = it.findViewById<ImageView>(R.id.img)
            iv?.setImageResource(com.snc.zero.resources.R.drawable.ic_camera)
            val text1 = it.findViewById<TextView>(R.id.text1)
            text1?.text = "blah blah (optional)"
            val text2 = it.findViewById<TextView>(R.id.text2)
            text2?.text = "blah blah blah blah blah blah"
        }

        val positiveButton = view.findViewById<AppCompatButton>(R.id.positiveButton)
        positiveButton?.let {
            it.setOnClickListener {
                activity?.apply {
                    if (this is NavActivity) {
                        if (isShowingBottomNav()) {
                            hideBottomNav()
                        } else {
                            showBottomNav()
                        }
                    }
                }
            }
        }
    }
}