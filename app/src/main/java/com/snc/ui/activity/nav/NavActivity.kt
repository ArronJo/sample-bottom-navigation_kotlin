package com.snc.ui.activity.nav

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.snc.configs.AppConfig
import com.snc.sample.bottom_navigation_kotlin.R
import com.snc.ui.activity.base.BaseAppCompatActivity
import com.snc.zero.lib.kotlin.backpressed.BackPressedCallbackCompat
import com.snc.zero.lib.kotlin.backpressed.listener.OnBackPressedCallbackListener
import com.snc.zero.ui.kotlin.extentions.getNavigationBarHeight
import timber.log.Timber

class NavActivity : BaseAppCompatActivity() {

    private lateinit var backKeyGuideToast: Toast

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private var navigationBarHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("[LifeCycle] NavActivity:: onCreate()")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)

        if (AppConfig.FEATURE_FULLSCREEN) {
            WindowCompat.setDecorFitsSystemWindows(window, false)

            try {
                findViewById<ViewGroup>(R.id.contentLayout)?.let {
                    ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                        Timber.i("NavHostContainer:: contentLayout - OnApplyWindowInsets")
                        navigationBarHeight = insets.getNavigationBarHeight()
                        return@setOnApplyWindowInsetsListener insets
                    }
                }

            } catch (e: Exception) {
                Timber.w(e)
            }
        }

        setupNavHost()
        setupOnBackPressedDispatcher()
    }

    private fun setupNavHost() {
        bottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.itemIconTintList = null

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.navHostContainer
        ) as NavHostFragment

        navController = navHostFragment.navController

        bottomNavigationView.setupWithNavController(navController)

        val badge = bottomNavigationView.getOrCreateBadge(R.id.nav_full_menu)
        badge.isVisible = true
        badge.number = 99

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            Timber.i("NavController::OnDestinationChangedListener(): destination=$destination, arguments=$arguments")

            when (destination.id) {
                R.id.myContractFragment, R.id.findProductFragment, R.id.newsFragment -> {
                    showBottomNav()
                }
                R.id.fullMenuFragment -> {
                    badge.isVisible = false
                    badge.clearNumber()

                    showBottomNav()
                }
                else -> {
                    hideBottomNav()
                }
            }

            if (AppConfig.DEBUG) {
                Timber.d("currentBackStackEntry : ${controller.currentBackStackEntry?.destination?.id}")
            }
        }
    }

    fun showBottomNav() {
        Timber.i("showBottomNav()")
        bottomNavigationView.visibility = View.VISIBLE

        if (AppConfig.FEATURE_FULLSCREEN) {
            findViewById<ViewGroup>(R.id.contentLayout)?.let {
                it.setPadding(it.paddingLeft, it.paddingTop, it.paddingRight, 0)
            }
        }
    }

    fun hideBottomNav() {
        Timber.i("hideBottomNav()")
        bottomNavigationView.visibility = View.GONE

        if (AppConfig.FEATURE_FULLSCREEN) {
            findViewById<ViewGroup>(R.id.contentLayout)?.let {
                it.setPadding(it.paddingLeft, it.paddingTop, it.paddingRight, navigationBarHeight)
            }
        }
    }

    fun isShowingBottomNav(): Boolean {
        return View.VISIBLE == bottomNavigationView.visibility
    }

    private fun setupOnBackPressedDispatcher() {
        backKeyGuideToast = Toast.makeText(
            this,
            com.snc.zero.resources.R.string.msg_one_more_press_back_button,
            Toast.LENGTH_SHORT
        )

        BackPressedCallbackCompat.addCallback(this, object : OnBackPressedCallbackListener {
            override fun handleOnBackPressed(twice: Boolean) {
                Timber.i("NavActivity:: onBackPressed : OnBackPressedCallbackListener()")
                handleOnBackPressedOnActivity(twice)
            }
        })

    }

    private fun handleOnBackPressedOnActivity(twice: Boolean) {
        when (navController.currentDestination?.id) {
            R.id.findProductFragment,
            R.id.myContractFragment,
            R.id.newsFragment,
            R.id.fullMenuFragment -> {
                if (twice) {
                    backKeyGuideToast.cancel()
                    finish()
                    return
                }
                backKeyGuideToast.show()
            }
            else -> {
                return
            }
        }
    }

}