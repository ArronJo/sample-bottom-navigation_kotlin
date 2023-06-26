@file:Suppress("MemberVisibilityCanBePrivate")

package com.snc.zero.ui.kotlin.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.snc.zero.ui.kotlin.R
import com.snc.zero.ui.kotlin.dialog.base.BaseDialog
import timber.log.Timber

class ModalBuilder(private val activity: Activity) {

    companion object {

        @JvmStatic
        fun with(activity: Activity): ModalBuilder {
            return ModalBuilder(activity)
        }

        @JvmStatic
        fun with(context: Context): ModalBuilder? {
            if (context is Activity) {
                return ModalBuilder(context)
            }
            return null
        }
    }

    private var contentViewLayout: Int = R.layout.view_modal_dialog_constraint_1_2_on_primary

    private var title: CharSequence? = null
    private var message: CharSequence? = null

    private var view: View? = null

    private var cancelable: Boolean = false

    private var onXButton: Boolean = false
    private var positiveButtonText: CharSequence? = null
    private var positiveButtonListener: DialogInterface.OnClickListener? = null
    private var negativeButtonText: CharSequence? = null
    private var negativeSpan: Any? = null
    private var negativeButtonListener: DialogInterface.OnClickListener? = null

    private var dimAmount = -1f

    fun setContentView(contentView: Int): ModalBuilder {
        this.contentViewLayout = contentView
        return this
    }

    fun setTitle(title: CharSequence?): ModalBuilder {
        this.title = title
        return this
    }

    fun setTitle(resId: Int): ModalBuilder {
        title = activity.resources.getString(resId)
        return this
    }

    fun setMessage(message: CharSequence?): ModalBuilder {
        this.message = message
        return this
    }

    fun setMessage(resId: Int): ModalBuilder {
        message = activity.resources.getString(resId)
        return this
    }

    fun setView(view: View?): ModalBuilder {
        this.view = view
        return this
    }

    fun setCancelable(cancelable: Boolean): ModalBuilder {
        this.cancelable = cancelable
        return this
    }

    fun setXButton(on: Boolean): ModalBuilder {
        this.onXButton = on
        return this
    }

    fun setPositiveButton(
        text: CharSequence? = null,
        listener: DialogInterface.OnClickListener?
    ): ModalBuilder {
        positiveButtonText = text
        positiveButtonListener = listener
        return this
    }

    fun setPositiveButton(resId: Int, listener: DialogInterface.OnClickListener?): ModalBuilder {
        return setPositiveButton(activity.resources.getString(resId), listener)
    }

    fun setNegativeButton(
        text: CharSequence? = null,
        span: Any? = null,
        listener: DialogInterface.OnClickListener?
    ): ModalBuilder {
        negativeButtonText = text
        negativeSpan = span
        negativeButtonListener = listener
        return this
    }

    fun setNegativeButton(
        resId: Int,
        span: Any? = null,
        listener: DialogInterface.OnClickListener?
    ): ModalBuilder {
        return setNegativeButton(activity.resources.getString(resId), span, listener)
    }

    fun setNegativeButton(resId: Int, listener: DialogInterface.OnClickListener?): ModalBuilder {
        return setNegativeButton(activity.resources.getString(resId), null, listener)
    }

    fun setNegativeButton(
        text: CharSequence? = null,
        listener: DialogInterface.OnClickListener?
    ): ModalBuilder {
        return setNegativeButton(text, null, listener)
    }

    fun setDimAmount(dimAmount: Float): ModalBuilder {
        this.dimAmount = dimAmount
        return this
    }

    private fun create(): Dialog? {
        if (activity.isFinishing) {
            Timber.e("activity is finished.")
            return null
        }

        return object : BaseDialog(activity) {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(contentViewLayout)

                setup()
            }

            private fun setup() {
                val dialog: Dialog = this

                dialog.setCancelable(cancelable)
                dialog.setCanceledOnTouchOutside(cancelable)

                val closeView = findViewById<ImageButton>(R.id.close)
                closeView?.let {
                    it.visibility = View.GONE
                    it.setOnClickListener {
                        dismiss()
                    }
                    if (onXButton && null != positiveButtonListener && null != negativeButtonListener) {
                        closeView.visibility = View.VISIBLE
                    }
                }

                val titleView = findViewById<TextView>(R.id.title)
                titleView?.let {
                    it.visibility = View.GONE
                    if (null != title) {
                        it.text = title
                        it.visibility = View.VISIBLE
                    }
                }

                val messageView = findViewById<TextView>(R.id.message)
                messageView?.let {
                    it.visibility = View.GONE
                    if (null != message) {
                        it.text = message
                        it.visibility = View.VISIBLE
                    }
                }

                view?.let {
                    val contentView = findViewById<LinearLayout>(R.id.content)

                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                    lp.gravity = Gravity.CENTER_HORIZONTAL
                    contentView?.addView(view, lp)
                }

                val positionButton = findViewById<Button>(R.id.positionButton)
                positionButton?.let {
                    positionButton.visibility = View.GONE

                    positiveButtonListener?.let {
                        positiveButtonText?.let {
                            positionButton.text = positiveButtonText
                        }
                        positionButton.setOnClickListener {
                            dismiss()
                            positiveButtonListener?.onClick(dialog, 0)
                        }
                        positionButton.visibility = View.VISIBLE
                    }
                }

                val negativeButton = findViewById<Button>(R.id.negativeButton)
                negativeButton?.let {
                    negativeButton.visibility = View.GONE

                    negativeButtonListener?.let {
                        negativeButtonText?.let {
                            negativeButton.text = negativeButtonText
                        }
                        negativeSpan?.let {
                            val sb = SpannableStringBuilder(negativeButton.text)
                            sb.setSpan(
                                negativeSpan, 0, negativeButton.text.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            negativeButton.text = sb
                        }
                        negativeButton.setOnClickListener {
                            dismiss()
                            negativeButtonListener?.onClick(dialog, 1)
                        }
                        negativeButton.visibility = View.VISIBLE
                    }
                }

                if (null == positiveButtonListener && null == negativeButtonListener) {
                    positionButton?.let {
                        positionButton.setText(com.snc.zero.resources.R.string.ok)
                        positionButton.setOnClickListener { dismiss() }
                        positionButton.visibility = View.VISIBLE
                    }
                    negativeButton?.let {
                        negativeButton.visibility = View.GONE
                    }
                }
            }
        }
    }

    fun show(): Dialog? {
        val dialog = create()
        dialog?.let {
            if (dimAmount >= 0) {
                dialog.window?.setDimAmount(dimAmount)
            }
            it.show()
        }
        return dialog
    }
}