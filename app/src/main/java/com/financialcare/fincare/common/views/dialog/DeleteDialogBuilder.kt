package com.financialcare.fincare.common.views.dialog

import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.fincare.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DeleteDialogBuilder {
    fun create(
        context: Context,
        @StringRes title: Int,
        @StringRes message: Int,
        onConfirm: () -> Unit,
        onCancel: () -> Unit
    ): AlertDialog {
        val dialog = MaterialAlertDialogBuilder(context)
            .setCancelable(false)
            .setTitle(context.getString(title))
            .setMessage(context.getString(message))
            .setPositiveButton(context.getString(R.string.delete)) { dialog, _ ->
                onConfirm()
                dialog.dismiss()
            }
            .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                onCancel()
                dialog.dismiss()
            }
            .create()

        dialog.setOnShowListener {
            val textColor = ContextCompat.getColor(context, R.color.red)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(textColor)
        }

        return dialog
    }
}