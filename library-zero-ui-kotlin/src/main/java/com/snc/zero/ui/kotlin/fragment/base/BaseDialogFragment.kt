package com.snc.zero.ui.kotlin.fragment.base

import android.content.DialogInterface
import androidx.fragment.app.DialogFragment

abstract class BaseDialogFragment : DialogFragment() {

    private var onDismissListener: DialogInterface.OnDismissListener? = null

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss(dialog)
    }
}