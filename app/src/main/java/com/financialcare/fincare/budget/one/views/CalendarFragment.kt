package com.financialcare.fincare.budget.one.views

import java.time.YearMonth
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fincare.R
import com.example.fincare.databinding.FragmentCalendarBinding
import com.financialcare.fincare.common.views.BaseFragment
import com.financialcare.fincare.common.views.calendar.CalendarAdapter
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable

@AndroidEntryPoint
class CalendarFragment(
    private val selectMonth: (YearMonth) -> Unit,
    private val selectedMonth: Observable<YearMonth>,
    private val minMonth: YearMonth,
    private val maxMonth: YearMonth
) : BaseFragment<FragmentCalendarBinding>(R.layout.fragment_calendar) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        val adapter = CalendarAdapter(
            selectMonth = selectMonth,
            notifyMonthChanged = binding.finCalendarView::notifyMonthChanged,
            selectedMonth = selectedMonth,
            context = requireContext(),
            compositeDisposable = compositeDisposable
        )

        binding.finCalendarView.setup(minMonth, maxMonth, true, adapter)

        return binding.root
    }
}