package com.financialcare.fincare.common.views.calendar

import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fincare.R
import com.example.fincare.databinding.CalendarViewBinding
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth

class CalendarView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    fun setup(startMonth: YearMonth, endMonth: YearMonth, disabledScroll: Boolean, adapter: CalendarAdapter) {
        binding.calendarView.setup(startMonth, endMonth, firstDayOfWeekFromLocale())

        val weekView = binding.llDaysOfWeek.children.map { it as TextView }.toList()
        weekView.forEachIndexed { index, textView ->
            val daysOfWeek = daysOfWeek()
            val dayOfWeek = daysOfWeek[index]
            textView.text =
                dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).replaceFirstChar(Char::uppercase)
        }

        if (disabledScroll) {
            binding.calendarView.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    return rv.scrollState == RecyclerView.SCROLL_STATE_DRAGGING
                }
            })
        }

        binding.calendarView.dayBinder = adapter.binder

        binding.increaseMonth = {
            val nextMonth = binding.calendarView.findFirstVisibleMonth()?.yearMonth?.nextMonth
            if (nextMonth != null && nextMonth <= endMonth) {
                adapter.selectMonth(nextMonth)
            }
        }

        binding.decreaseMonth = {
            val previousMonth = binding.calendarView.findFirstVisibleMonth()?.yearMonth?.previousMonth
            if (previousMonth != null && previousMonth >= startMonth) {
                adapter.selectMonth(previousMonth)
            }
        }
    }

    fun notifyMonthChanged(currentMonth: YearMonth) {
        val month = currentMonth.month
            .getDisplayName(TextStyle.SHORT, Locale.getDefault())
            .replaceFirstChar(Char::uppercase)

        binding.calendarView.notifyMonthChanged(currentMonth)
        binding.calendarView.smoothScrollToMonth(currentMonth)
        binding.currentMonth = "$month ${currentMonth.year}"
    }

    private val binding: CalendarViewBinding =
        DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.calendar_view, this, true)
}