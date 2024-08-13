package com.financialcare.fincare.common.time

import android.content.Context
import android.content.res.Resources
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.View
import android.widget.NumberPicker
import android.widget.TimePicker
import com.example.fincare.R

class TimePickerView(context: Context, attrs: AttributeSet) : TimePicker(context, attrs) {

    override fun setOnTimeChangedListener(onTimeChangedListener: OnTimeChangedListener) {
        super.setOnTimeChangedListener { timePicker, hour, minute ->
            onTimeChangedListener.onTimeChanged(timePicker, hour, minute * step)
        }
    }

    override fun getMinute(): Int {
        return super.getMinute() * step
    }

    override fun setMinute(minute: Int) {
        super.setMinute(minute / step)
    }

    private val step: Int

    init {
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.TimePickerView, 0, 0)
        step = attributes.getInteger(R.styleable.TimePickerView_minuteStep, 1)
        val minValue = 0
        val maxValue = 59 / step

        val minutePickerId = Resources.getSystem().getIdentifier("minute", "id", "android")
        val minutePicker = findViewById<View>(minutePickerId) as NumberPicker
        val displayedValues = (minValue..maxValue).map { (it * step).toString() }.toTypedArray()

        minutePicker.minValue = minValue
        minutePicker.maxValue = maxValue
        minutePicker.displayedValues = displayedValues
        descendantFocusability = FOCUS_BLOCK_DESCENDANTS
        setIs24HourView(DateFormat.is24HourFormat(context))
    }
}