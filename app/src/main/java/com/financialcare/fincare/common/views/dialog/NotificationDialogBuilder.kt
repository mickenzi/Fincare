package com.financialcare.fincare.common.views.dialog

import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object NotificationDialogBuilder {
    fun create(context: Context, confirmId: Int, onConfirm: (DialogInterface) -> Unit): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setCancelable(false)
            .setPositiveButton(context.getString(confirmId)) { dialog, _ ->
                onConfirm(dialog)
            }
            .create()
    }

    fun AlertDialog.show(@StringRes titleId: Int, @StringRes messageId: Int) {
        this
            .apply {
                setTitle(context.getString(titleId))
                setMessage(context.getString(messageId))
            }
            .show()
    }
}