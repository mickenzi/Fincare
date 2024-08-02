package com.financialcare.fincare.common.views.dialog

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.fincare.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object LoadingDialogBuilder {
    fun create(context: Context): AlertDialog {
        return MaterialAlertDialogBuilder(context, R.style.wrap_content_dialog)
            .setView(R.layout.fragment_loading_dialog)
            .setCancelable(false)
            .create()
    }
}