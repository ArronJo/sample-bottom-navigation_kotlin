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
import com.snc.ui.activity.base.BaseAppCompatActivity
import com.snc.sample.bottom_navigation_kotlin.R
import com.snc.zero.ui.kotlin.extentions.getImeHeight
import com.snc.zero.ui.kotlin.extentions.getNavigationBarHeight
import com.snc.zero.ui.kotlin.extentions.getNavigationBarImeHeight
import com.snc.zero.ui.kotlin.extentions.postDelayed
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
            setupFullScreen()
        }

        setupNavHost()
        setupOnBackPressedDispatcher()
    }

    private fun setupFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val contentView = findViewById<ViewGroup>(R.id.fitLayout)
        contentView?.let {
            val navHostContainer = findViewById<ViewGroup>(R.id.nav_host_container)
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                if (insets.getImeHeight() > 0) {
                    hideBottomNav()
                    val paddingBottom = insets.getNavigationBarImeHeight()
                    Timber.i("NavHostContainer::setPadding + Ime(${v.paddingLeft}, ${v.paddingTop}, ${v.paddingRight}, ${paddingBottom})")
                    navHostContainer.setPadding(
                        v.paddingLeft,
                        v.paddingTop,
                        v.paddingRight,
                        paddingBottom
                    )
                } else {
                    val destinationId = navController.currentDestination?.id
                    if (R.id.myContractFragment != destinationId
                        && R.id.findProductFragment != destinationId
                        && R.id.fullMenuFragment != destinationId
                    ) {
                        return@setOnApplyWindowInsetsListener insets
                    }

                    showBottomNav()

                    val navigationBarHeight = resources.getNavigationBarHeight()
                    val bottomNavView = findViewById<ViewGroup>(R.id.bottom_nav)
                    var paddingBottom = 0
                    if (0 == navigationBarHeight) {
                        paddingBottom = bottomNavView.height
                    }
                    Timber.i("NavHostContainer::setPadding(${v.paddingLeft}, ${v.paddingTop}, ${v.paddingRight}, ${v.paddingBottom})")
                    navHostContainer?.setPadding(
                        v.paddingLeft,
                        v.paddingTop,
                        v.paddingRight,
                        paddingBottom + navigationBarHeight
                    )
                }
                return@setOnApplyWindowInsetsListener insets
            }
        }
    }

    private fun setupNavHost() {
        bottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.itemIconTintList = null

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_container
        ) as NavHostFragment

        navController = navHostFragment.navController

        bottomNavigationView.setupWithNavController(navController)

        val badge = bottomNavigationView.getOrCreateBadge(R.id.menu_full_menu)
        badge.isVisible = true
        badge.number = 99

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            Timber.i("NavController::OnDestinationChangedListener(): destination=$destination, arguments=$arguments")

            when (destination.id) {
                R.id.myContractFragment, R.id.findProductFragment -> {
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
                Timber.d("currentBackStackEntry : ${controller.currentBackStackEntry?.destination?.displayName}")
            }
        }
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
            R.id.findProductFragment, R.id.myContractFragment, R.id.fullMenuFragment -> {
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

    fun showBottomNav() {
        Timber.i("showBottomNav()")
        bottomNavigationView.visibility = View.VISIBLE

        postDelayed({
            val navigationBarHeight = resources.getNavigationBarHeight()
            val bottomNavView = findViewById<ViewGroup>(R.id.bottom_nav)
            var paddingBottom = 0
            if (0 == navigationBarHeight) {
                paddingBottom = bottomNavView.height
            }

            val navHostContainer = findViewById<ViewGroup>(R.id.nav_host_container)
            navHostContainer.setPadding(
                navHostContainer.paddingLeft,
                navHostContainer.paddingTop,
                navHostContainer.paddingRight,
                paddingBottom + navigationBarHeight
            )
        }, 100)
    }

    fun hideBottomNav() {
        Timber.i("hideBottomNav()")
        bottomNavigationView.visibility = View.GONE

        val navigationBarHeight = resources.getNavigationBarHeight()
        val navHostContainer = findViewById<ViewGroup>(R.id.nav_host_container)
        navHostContainer?.setPadding(
            navHostContainer.paddingLeft,
            navHostContainer.paddingTop,
            navHostContainer.paddingRight,
            navigationBarHeight
        )
    }

    fun isShowingBottomNav(): Boolean {
        return View.VISIBLE == bottomNavigationView.visibility
    }
}