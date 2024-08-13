package com.financialcare.fincare.common.views.calendar

import java.time.LocalDate
import java.time.YearMonth
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.example.fincare.R
import com.example.fincare.databinding.FragmentCalendarDayItemBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo

class CalendarAdapter(
    private val selectMonth: (YearMonth) -> Unit,
    private val notifyMonthChanged: (YearMonth) -> Unit,
    selectedMonth: Observable<YearMonth>,
    context: Context,
    compositeDisposable: CompositeDisposable
) {
    fun selectMonth(month: YearMonth) = this.selectMonth.invoke(month)

    val binder: MonthDayBinder<ViewContainer> = object : MonthDayBinder<ViewContainer> {
        override fun create(view: View): ViewContainer = DayViewHolder(view)

        override fun bind(container: ViewContainer, data: CalendarDay) {
            val holder = container as DayViewHolder
            val binding = holder.binding

            val isCurrentMonth = data.position == DayPosition.MonthDate
            binding.llCalendarDayItem.visibility = if (isCurrentMonth) View.VISIBLE else View.INVISIBLE

            val isToday = data.date == LocalDate.now()
            binding.llCalendarDayItem.background =
                if (isToday) ContextCompat.getDrawable(context, R.drawable.calendar_today_bg) else null

            binding.date = data.date.dayOfMonth.toString()
        }
    }

    class DayViewHolder(view: View) : ViewContainer(view) {
        val binding = FragmentCalendarDayItemBinding.bind(view)
    }

    init {
        selectedMonth
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                notifyMonthChanged(it)
            }
            .addTo(compositeDisposable)
    }
}