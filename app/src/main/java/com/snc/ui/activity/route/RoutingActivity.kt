package com.snc.ui.activity.route

import android.content.Intent
import android.os.Bundle
import com.snc.ui.activity.base.BaseAppCompatActivity
import com.snc.ui.activity.nav.NavActivity
import timber.log.Timber

class RoutingActivity : BaseAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("[LifeCycle] RoutingActivity:: onCreate()")

        super.onCreate(savedInstanceState)

        val intent = Intent(this, NavActivity::class.java)
        startActivity(intent)
        finish()
    }
}