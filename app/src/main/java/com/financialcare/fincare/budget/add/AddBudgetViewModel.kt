package com.financialcare.fincare.budget.add

import java.time.YearMonth
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
class AddBudgetViewModel @Inject constructor(
    private val budgetsRepository: BudgetsRepository
) : ViewModel() {
    fun add() {
        addSubject.onNext(Unit)
    }

    fun setIncome(income: String) {
        incomeSubject.onNext(income)
    }

    fun setMonth(month: YearMonth) {
        monthSubject.onNext(month)
    }

    val savedBudget: Observable<Unit>
    val error: Observable<BudgetsError>

    val isIncomeValid: Observable<Boolean>

    val isLoading: Observable<Boolean>

    private val incomeSubject = BehaviorSubject.create<String>()
    private val monthSubject = BehaviorSubject.create<YearMonth>()

    private val addSubject = PublishSubject.create<Unit>()

    init {
        val selectedIncome = incomeSubject.flatMap { it.toLongOrNull()?.let(::just) ?: empty() }

        val savedBudget = addSubject
            .withLatestFrom(
                selectedIncome,
                monthSubject
            ) { _, income, month -> Pair(income, month) }
            .switchMapSingle { (income, month) ->
                budgetsRepository
                    .add(Budget(income = income, month = month))
                    .either<Unit, BudgetsError>()
            }
            .replay(1)
            .refCount()

        this.savedBudget = savedBudget.filterRight()

        error = savedBudget.filterLeft()

        isIncomeValid = incomeSubject.map { income ->
            income.toLongOrNull()?.let { it > 0L } ?: false
        }

        isLoading = addSubject
            .map { true }
            .mergeWith(savedBudget.map { false })
    }
}