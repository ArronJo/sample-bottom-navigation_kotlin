package com.snc.zero.ui.kotlin.util

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.provider.Settings
import android.widget.ProgressBar
import androidx.core.animation.addListener
import timber.log.Timber

class ProgressBarUtil {

    companion object {

        private var animator: ObjectAnimator? = null

        @JvmStatic
        fun startAnimLoop(
            target: ProgressBar,
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

            animator = ObjectAnimator.ofInt(target, "progress", target.progress, target.max)
            animator?.let { animator ->
                animator.duration = duration
                animator.repeatMode = ValueAnimator.RESTART
                animator.repeatCount = repeatCount

                // Get duration scale from the global settings.
                val durationScale: Float = Settings.Global.getFloat(
                    target.context.contentResolver,
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

        @JvmStatic
        fun stopAnimLoop() {
            animator?.cancel()
            animator = null
        }
    }
}