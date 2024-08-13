package com.financialcare.fincare.budget.one

import java.time.YearMonth
import java.util.Optional
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import com.financialcare.fincare.budget.Budget
import com.financialcare.fincare.budget.BudgetsError
import com.financialcare.fincare.budget.BudgetsRepository
import com.financialcare.fincare.common.rx.either
import com.financialcare.fincare.common.rx.filterLeft
import com.financialcare.fincare.common.rx.filterRight
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observable.empty
import io.reactivex.rxjava3.core.Observable.just
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetsRepository: BudgetsRepository
) : ViewModel() {
    fun add() {
        addSubject.onNext(Unit)
    }

    fun edit() {
        selectSubject.onNext(Unit)
    }

    fun selectMonth(month: YearMonth) {
        monthSubject.onNext(month)
    }

    fun reload() {
        reloadSubject.onNext(Unit)
    }

    val budget: Observable<Pair<Optional<Budget>, YearMonth>>
    val initBudget: Observable<YearMonth>
    val error: Observable<BudgetsError>

    val selectedBudget: Observable<YearMonth>

    val selectedMonth: Observable<YearMonth>

    val minMonth: YearMonth
    val maxMonth: YearMonth

    private val monthSubject = BehaviorSubject.create<YearMonth>()

    private val addSubject = PublishSubject.create<Unit>()

    private val selectSubject = PublishSubject.create<Unit>()

    private val reloadSubject = PublishSubject.create<Unit>()

    init {
        val now = YearMonth.now()
        minMonth = now.minusYears(1).withMonth(1)
        maxMonth = now.plusMonths(1)

        selectedMonth = monthSubject
            .startWithItem(now)

        val budget = Observable
            .combineLatest(
                selectedMonth,
                reloadSubject.startWithItem(Unit)
            ) { month, _ -> month }
            .switchMapSingle { month ->
                budgetsRepository
                    .month(month.monthValue)
                    .either<Optional<Budget>, BudgetsError>()
            }
            .replay(1)
            .refCount()

        initBudget = budget
            .filterRight()
            .take(1)
            .withLatestFrom(selectedMonth, ::Pair)
            .flatMap { (optBudget, month) ->
                if (!optBudget.isPresent && month == now) just(now) else empty()
            }
            .mergeWith(addSubject.withLatestFrom(selectedMonth) { _, month -> month })

        this.budget = budget
            .filterRight()
            .withLatestFrom(selectedMonth, ::Pair)

        error = budget.filterLeft()

        selectedBudget = selectSubject
            .withLatestFrom(selectedMonth) { _, month -> month }
    }
}