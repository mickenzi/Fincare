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
import io.reactivex.rxjava3.subjects.BehaviorSubject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetsRepository: BudgetsRepository
) : ViewModel() {

    val budget: Observable<Optional<Budget>>
    val error: Observable<BudgetsError>

    private val monthSubject = BehaviorSubject.createDefault<YearMonth>(YearMonth.now())

    init {
        val budget = monthSubject
            .switchMapSingle { month ->
                budgetsRepository
                    .month(month.monthValue)
                    .either<Optional<Budget>, BudgetsError>()
            }
            .replay(1)
            .refCount()

        this.budget = budget.filterRight()

        error = budget.filterLeft()
    }
}