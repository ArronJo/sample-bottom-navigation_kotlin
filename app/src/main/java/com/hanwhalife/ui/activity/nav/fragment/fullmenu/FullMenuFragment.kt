package com.hanwhalife.ui.activity.nav.fragment.fullmenu

import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hanwhalife.ui.activity.adapter.PackageInfoAdapter
import com.hanwhalife.ui.activity.nav.NavActivity
import com.hanwhalife.ui.activity.nav.bizconst.BizConst
import com.hanwhalife.ui.activity.nav.fragment.base.BaseNavFragment
import com.snc.sample.bottom_navigation_kotlin.R
import com.snc.zero.lib.kotlin.util.PackageUtil
import com.snc.zero.ui.kotlin.extentions.navigateWithDefaultAnimation
import com.snc.zero.ui.kotlin.extentions.postDelayed
import com.snc.zero.ui.kotlin.util.IntentUtil
import timber.log.Timber

class FullMenuFragment : BaseNavFragment(R.layout.fragment_nav_full_menu) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackPressedDispatcher = requireActivity().onBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Timber.i("FullMenuFragment:: onBackPressed : handleOnBackPressed() : isEnabled = $isEnabled")
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
        Timber.i("FullMenuFragment:: arguments : ${BizConst.ARGUMENT_KEY_PARAM} = $params")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupList(view)
        setupButtons(view)
    }

    private fun setupList(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.app_list)
        recyclerView?.let { v ->
            v.itemAnimator = null
            v.layoutManager = LinearLayoutManager(context)

            var adapter: PackageInfoAdapter? = null
            adapter = PackageInfoAdapter(
                activity,
                v,
                listItems = null,
                callback = object : PackageInfoAdapter.Callback {
                    override fun onRemoveClicked(position: Int) {
                        adapter?.getItems()?.let { item ->
                            context?.let { context ->
                                IntentUtil.showGotoSettingsDialog(
                                    context,
                                    item[position].packageName
                                )
                            }
                        }
                    }
                })
            v.adapter = adapter

            postDelayed({
                context?.let { context ->
                    val list: ArrayList<PackageInfo> =
                        PackageUtil.getInstalledPackageInfo(context, true)
                    adapter.submit(list)
                }

            }, 500)
        }
    }

    private fun setupButtons(view: View) {
        val rightButton = view.findViewById<Button>(R.id.right)
        rightButton?.setOnClickListener {
            goToBizAppWeb()
        }
    }

    private fun goToBizAppWeb() {
        activity?.apply {
            if (this is NavActivity) {
                hideBottomNav()
            }
        }

        findNavController().navigateWithDefaultAnimation(
            R.id.bizWebFragment,
            bundleOf(BizConst.ARGUMENT_KEY_URL to "https://google.com"),
            NavOptions.Builder()
        )
    }
}
