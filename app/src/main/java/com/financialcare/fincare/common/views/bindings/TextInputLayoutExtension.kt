package com.financialcare.fincare.common.views.bindings

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import com.google.android.material.textfield.TextInputLayout

object TextInputLayoutExtension {
    fun TextInputLayout.setValidStyle() {
        val endIcon = if (errorIconDrawable == null) endIconDrawable else null

        updateStyleComponents(endIcon, null, null)
    }

    fun TextInputLayout.setInvalidStyle(errorText: String) {
        val colorFilter = boxStrokeErrorColor?.defaultColor?.let { defColor ->
            PorterDuffColorFilter(
                defColor,
                PorterDuff.Mode.MULTIPLY
            )
        }
        val endIcon = if (errorIconDrawable == null) endIconDrawable else null

        updateStyleComponents(endIcon, errorText, colorFilter)
    }

    private fun TextInputLayout.updateStyleComponents(
        endIcon: Drawable?,
        errorText: String?,
        colorFilter: PorterDuffColorFilter?
    ) {
        endIconDrawable = endIcon
        error = errorText
        startIconDrawable?.colorFilter = colorFilter
    }
}