package com.snc.ui.activity.nav

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.snc.consts.AppConfig
import com.snc.sample.bottom_navigation_kotlin.R
import com.snc.ui.activity.base.BaseAppCompatActivity
import timber.log.Timber

class NavActivity : BaseAppCompatActivity() {

    companion object {
        const val TIME_INTERVAL_BACK_KEY_TWICE = 2000L
    }

    private var backKeyPressedTime = 0L
    private lateinit var backKeyGuideToast: Toast

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("[LifeCycle] NavActivity:: onCreate()")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)

        if (AppConfig.FEATURE_FULLSCREEN) {
            WindowCompat.setDecorFitsSystemWindows(window, false)

            try {
                findViewById<ViewGroup>(android.R.id.content).let {
                    ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                        Timber.i("NavHostContainer:: rootView - OnApplyWindowInsets")
                        return@setOnApplyWindowInsetsListener insets
                    }
                }

                findViewById<ViewGroup>(R.id.contentLayout)?.let {
                    ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                        Timber.i("NavHostContainer:: contentLayout - OnApplyWindowInsets")
                        return@setOnApplyWindowInsetsListener insets
                    }
                }

                findViewById<ViewGroup>(R.id.fitLayout)?.let {
                    ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                        Timber.i("NavHostContainer:: fitLayout - OnApplyWindowInsets")
                        return@setOnApplyWindowInsetsListener insets
                    }
                }

                findViewById<ViewGroup>(R.id.nav_host_container)?.let {
                    ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                        Timber.i("NavHostContainer:: navHostContainer - OnApplyWindowInsets")
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
            R.id.nav_host_container
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
    }

    fun hideBottomNav() {
        Timber.i("hideBottomNav()")
        bottomNavigationView.visibility = View.GONE
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

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Timber.i("NavActivity:: onBackPressed : handleOnBackPressed() : isEnabled = $isEnabled")
                if (isEnabled) {
                    handleOnBackPressedOnActivity()
                } else {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun handleOnBackPressedOnActivity() {
        when (navController.currentDestination?.id) {
            R.id.findProductFragment,
            R.id.myContractFragment,
            R.id.newsFragment,
            R.id.fullMenuFragment -> {
                if (System.currentTimeMillis() > backKeyPressedTime + TIME_INTERVAL_BACK_KEY_TWICE) {
                    backKeyPressedTime = System.currentTimeMillis()
                    backKeyGuideToast.show()
                    return
                }
                if (System.currentTimeMillis() <= backKeyPressedTime + TIME_INTERVAL_BACK_KEY_TWICE) {
                    backKeyGuideToast.cancel()
                    finish()
                }
            }

            R.id.splashFragment -> {
                return
            }
        }
    }

}