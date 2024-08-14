package com.financialcare.fincare.reports.list

import java.time.YearMonth
import javax.inject.Inject
import kotlin.math.floor
import androidx.lifecycle.ViewModel
import com.financialcare.fincare.common.rx.either
import com.financialcare.fincare.common.rx.filterLeft
import com.financialcare.fincare.common.rx.filterRight
import com.financialcare.fincare.reports.Budget
import com.financialcare.fincare.reports.ReportsError
import com.financialcare.fincare.reports.ReportsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val reportsRepository: ReportsRepository
) : ViewModel() {
    fun setYear(year: Int) {
        yearSubject.onNext(year)
    }

    fun increaseYear() {
        increaseYearSubject.onNext(Unit)
    }

    fun decreaseYear() {
        decreaseYearSubject.onNext(Unit)
    }

    val reports: Observable<List<Budget>>
    val error: Observable<ReportsError>

    val kinds: Observable<List<Pair<String, Double>>>

    val year: Observable<Int>
    val months: List<Int>

    private val yearSubject = BehaviorSubject.create<Int>()

    private val increaseYearSubject = PublishSubject.create<Unit>()
    private val decreaseYearSubject = PublishSubject.create<Unit>()

    init {
        val now = YearMonth.now()

        val months = (1..12).map { YearMonth.of(now.year, it) }
        this.months = months.map(YearMonth::getMonthValue)

        year = increaseYearSubject
            .withLatestFrom(yearSubject) { _, year -> year.plus(1) }
            .mergeWith(decreaseYearSubject.withLatestFrom(yearSubject) { _, year -> year.minus(1) })
            .startWithItem(now.year)

        val reports = yearSubject
            .switchMapSingle { year ->
                reportsRepository
                    .monthly(year)
                    .either<List<Budget>, ReportsError>()
            }
            .replay(1)
            .refCount()

        this.reports = reports
            .filterRight()
            .observeOn(Schedulers.computation())
            .map { reps ->
                val repsMap = reps.associateBy(Budget::month)
                months
                    .associateWith { repsMap.getOrDefault(it, Budget(month = it)) }
                    .values
                    .toList()
            }

        error = reports.filterLeft()

        kinds = this.reports
            .map { reps ->
                val totalCount = reps.mapNotNull(Budget::maxExpenseKind).size

                val kinds = reps
                    .groupBy { it.maxExpenseKind ?: NONE }
                    .filter { it.key != NONE }
                    .entries
                    .sortedByDescending { it.value.size }
                    .map { Pair(it.key, it.value.size) }
                    .take(3)

                kinds.map { Pair(it.first, floor((it.second * 100).toDouble() / totalCount)) }
            }
    }

    private companion object {
        private const val NONE = "none"
    }
}