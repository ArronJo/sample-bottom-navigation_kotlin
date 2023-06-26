package com.snc.zero.ui.kotlin.extentions

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Build
import android.provider.Settings
import android.widget.ProgressBar
import androidx.core.animation.addListener
import timber.log.Timber

private var anim: ObjectAnimator? = null

private var animator: ObjectAnimator? = null

fun ProgressBar.startAnimLoop(
    duration: Long = 2000L,
    repeatCount: Int = ValueAnimator.INFINITE,
    listener: (String) -> Unit = {}
) {
    animator?.let {
        if (it.isRunning) {
            Timber.i("onProgressChanged(): animator is running...")
            return@startAnimLoop
        }
    }

    animator = ObjectAnimator.ofInt(this, "progress", progress, max)
    animator?.let { animator ->
        animator.duration = duration
        animator.repeatMode = ValueAnimator.RESTART
        animator.repeatCount = repeatCount

        // Get duration scale from the global settings.
        val durationScale: Float = Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE, 0.0f
        )

        if (durationScale != 1f) {
            try {
                ValueAnimator::class.java.getMethod(
                    "setDurationScale",
                    Float::class.javaPrimitiveType
                ).invoke(null, 1f)

            } catch (t: Throwable) {
                Timber.i(t)
            }
        }

        listener.let {
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    Timber.i("onProgressChanged(): onAnimationStart...")
                    listener("start")
                }

                override fun onAnimationEnd(animation: Animator) {
                    Timber.i("onProgressChanged(): onAnimationEnd...")
                    listener("end")
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }

            })
        }
        animator.addListener()
        animator.start()
    }
}

fun ProgressBar.stopAnimLoop() {
    animator?.cancel()
    animator = null
}

fun ProgressBar.setProgressWithAnim(value: Int): ProgressBar {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        setProgress(value, true)
    } else {
        progress = value
    }
    return this
}
