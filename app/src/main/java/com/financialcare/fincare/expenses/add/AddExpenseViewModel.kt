package com.financialcare.fincare.expenses.add

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.YearMonth
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import com.financialcare.fincare.common.rx.either
import com.financialcare.fincare.common.rx.filterLeft
import com.financialcare.fincare.common.rx.filterRight
import com.financialcare.fincare.expenses.ExpensesError
import com.financialcare.fincare.expenses.ExpensesRepository
import com.kizitonwose.calendar.core.atStartOfMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val expensesRepository: ExpensesRepository
) : ViewModel() {
    fun create() {
        createSubject.onNext(Unit)
    }

    fun selectKind(kind: String) {
        kindSubject.onNext(kind)
    }

    fun setAmount(amount: String) {
        amountSubject.onNext(amount)
    }

    fun selectDate(date: LocalDate) {
        dateSubject.onNext(date)
    }

    fun selectTime(time: LocalTime) {
        timeSubject.onNext(time)
    }

    fun startDateSelection() {
        isDateSelectionActiveSubject.onNext(true)
    }

    fun dismissDateSelection() {
        isDateSelectionActiveSubject.onNext(false)
    }

    fun startTimeSelection() {
        isTimeSelectionActiveSubject.onNext(true)
    }

    fun dismissTimeSelection() {
        isTimeSelectionActiveSubject.onNext(false)
    }

    val createdExpense: Observable<Unit>
    val error: Observable<ExpensesError>

    val kind: Observable<String>
    val amount: Observable<String>
    val date: Observable<LocalDate>
    val time: Observable<LocalTime>

    val dateSelection: Observable<LocalDate>
    val timeSelection: Observable<LocalTime>

    val isAmountValid: Observable<Boolean>

    val areExpenseDataValid: Observable<Boolean>

    val minDate: LocalDate
    val maxDate: LocalDate

    val isLoading: Observable<Boolean>

    private val kindSubject = BehaviorSubject.create<String>()
    private val amountSubject = BehaviorSubject.create<String>()
    private val dateSubject = BehaviorSubject.create<LocalDate>()
    private val timeSubject = BehaviorSubject.create<LocalTime>()

    private val isTimeSelectionActiveSubject = PublishSubject.create<Boolean>()
    private val isDateSelectionActiveSubject = PublishSubject.create<Boolean>()

    private val createSubject = PublishSubject.create<Unit>()

    init {
        val now = LocalDate.now()

        maxDate = now

        minDate = YearMonth.now().atStartOfMonth()

        kind = kindSubject

        amount = amountSubject

        time = timeSubject

        date = dateSubject

        timeSelection = isTimeSelectionActiveSubject
            .distinctUntilChanged()
            .filter { it }
            .withLatestFrom(time.startWithItem(LocalTime.now())) { _, time -> time }

        dateSelection = isDateSelectionActiveSubject
            .distinctUntilChanged()
            .filter { it }
            .withLatestFrom(date.startWithItem(now)) { _, date -> date }

        isAmountValid = amountSubject.map { am ->
            am.toLongOrNull()?.let { it > 0L } ?: false
        }

        areExpenseDataValid = Observable
            .combineLatest(
                isAmountValid.startWithItem(false),
                kindSubject.map { !it.isNullOrBlank() }.startWithItem(false),
                dateSubject.map { true }.startWithItem(false),
                timeSubject.map { true }.startWithItem(false)
            ) { iav, ikv, idv, itv -> iav && ikv && idv && itv }

        val dateTime = Observable
            .combineLatest(
                dateSubject,
                timeSubject
            ) { date, time ->
                val offset = OffsetDateTime.now().offset
                OffsetDateTime.of(LocalDateTime.of(date, time), offset)
            }

        val createdExpense = createSubject
            .withLatestFrom(
                kindSubject,
                amountSubject,
                dateTime
            ) { _, kind, amount, dt -> Triple(kind, amount, dt) }
            .switchMapSingle { (kind, amount, dt) ->
                expensesRepository
                    .add(dt, amount.toLong(), kind)
                    .either<Unit, ExpensesError>()
            }
            .replay(1)
            .refCount()

        this.createdExpense = createdExpense.filterRight()

        error = createdExpense.filterLeft()

        isLoading = createSubject
            .map { true }
            .mergeWith(this.createdExpense.map { false })
            .mergeWith(error.map { false })
    }
}