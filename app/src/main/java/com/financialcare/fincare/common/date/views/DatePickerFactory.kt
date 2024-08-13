package com.financialcare.fincare.common.date.views

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import android.content.Context
import com.example.fincare.R
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker

object DatePickerFactory {
    fun createSinglePicker(
        context: Context,
        minDate: LocalDate,
        maxDate: LocalDate,
        initDate: LocalDate,
        onSelect: (LocalDate) -> Unit,
        onDismiss: () -> Unit
    ): MaterialDatePicker<Long> {
        val zoneOffset = ZoneOffset.UTC

        val minValue = minDate.atStartOfDay().toInstant(zoneOffset).toEpochMilli()
        val maxValue = maxDate.plusDays(1).atStartOfDay().minusMinutes(1).toInstant(zoneOffset).toEpochMilli()

        val validators = listOf(DateValidatorPointForward.from(minValue), DateValidatorPointBackward.before(maxValue))

        val calendarConstraintBuilder = CalendarConstraints
            .Builder()
            .setStart(minValue)
            .setEnd(maxValue)
            .setOpenAt(initDate.atStartOfDay().toInstant(zoneOffset).toEpochMilli())
            .setValidator(CompositeDateValidator.allOf(validators))
            .build()

        val materialDatePicker = MaterialDatePicker.Builder
            .datePicker()
            .setTheme(R.style.date_selection_dialog)
            .setTitleText(context.getString(R.string.select_date))
            .setCalendarConstraints(calendarConstraintBuilder)
            .setSelection(initDate.atStartOfDay().toInstant(zoneOffset).toEpochMilli())
            .build()

        materialDatePicker.addOnPositiveButtonClickListener { selection ->
            fromEpoch(selection)?.let(onSelect)
        }

        materialDatePicker.addOnDismissListener {
            onDismiss()
        }

        return materialDatePicker
    }

    private fun fromEpoch(epoch: Long?): LocalDate? {
        return epoch?.let { ep ->
            Instant.ofEpochMilli(ep)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()
        }
    }
}