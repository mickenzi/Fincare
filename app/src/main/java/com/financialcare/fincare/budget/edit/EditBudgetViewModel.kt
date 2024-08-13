package com.financialcare.fincare.budget.edit

import java.time.YearMonth
import java.util.Optional
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import com.financialcare.fincare.budget.Budget
import com.financialcare.fincare.budget.BudgetsError
import com.financialcare.fincare.budget.BudgetsRepository
import com.financialcare.fincare.common.optional.fold
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
class EditBudgetViewModel @Inject constructor(
    private val budgetsRepository: BudgetsRepository
) : ViewModel() {
    fun setMonth(month: YearMonth) {
        monthSubject.onNext(month)
    }

    fun setIncome(income: String) {
        incomeSubject.onNext(income)
    }

    fun save() {
        saveSubject.onNext(Unit)
    }

    val budget: Observable<Budget>
    val error: Observable<BudgetsError>

    val savedBudget: Observable<Unit>

    val isIncomeValid: Observable<Boolean>

    private val monthSubject = BehaviorSubject.create<YearMonth>()

    private val incomeSubject = BehaviorSubject.create<String>()

    private val saveSubject = PublishSubject.create<Unit>()

    init {
        val loadedBudget = monthSubject
            .switchMapSingle { month ->
                budgetsRepository
                    .month(month.monthValue)
                    .either<Optional<Budget>, BudgetsError>()
            }
            .replay(1)
            .refCount()

        this.budget = loadedBudget.filterRight().flatMap { it.fold(::empty, ::just) }

        val income = incomeSubject.flatMap { it.toLongOrNull()?.let(::just) ?: empty() }

        val savedBudget = saveSubject
            .withLatestFrom(income, monthSubject) { _, inc, month -> Pair(inc, month) }
            .switchMapSingle { (income, month) ->
                budgetsRepository
                    .edit(month.year, month.monthValue, income)
                    .either<Unit, BudgetsError>()
            }
            .replay(1)
            .refCount()

        this.savedBudget = savedBudget.filterRight()

        error = loadedBudget
            .filterLeft()
            .mergeWith(savedBudget.filterLeft())

        isIncomeValid = incomeSubject.map { incomeOpt ->
            incomeOpt.toLongOrNull()?.let { it > 0L } ?: false
        }
    }
}