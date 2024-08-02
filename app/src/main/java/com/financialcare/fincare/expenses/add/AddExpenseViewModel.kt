package com.financialcare.fincare.expenses.add

import java.time.OffsetDateTime
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import com.financialcare.fincare.common.rx.either
import com.financialcare.fincare.common.rx.filterLeft
import com.financialcare.fincare.common.rx.filterRight
import com.financialcare.fincare.expenses.ExpensesError
import com.financialcare.fincare.expenses.ExpensesRepository
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

    val createdExpense: Observable<Unit>
    val error: Observable<ExpensesError>

    val kind: Observable<String>
    val amount: Observable<String>

    val isAmountValid: Observable<Boolean>

    val areExpenseDataValid: Observable<Boolean>

    val isLoading: Observable<Boolean>

    private val kindSubject = BehaviorSubject.create<String>()
    private val amountSubject = BehaviorSubject.create<String>()

    private val createSubject = PublishSubject.create<Unit>()

    init {
        kind = kindSubject

        amount = amountSubject

        isAmountValid = amountSubject.map { am ->
            am.toLongOrNull()?.let { it > 0L } ?: false
        }

        areExpenseDataValid = Observable
            .combineLatest(
                isAmountValid.startWithItem(false),
                kindSubject.map { !it.isNullOrBlank() }.startWithItem(false)
            ) { iav, ikv -> iav && ikv }

        val createdExpense = createSubject
            .withLatestFrom(
                kindSubject,
                amountSubject
            ) { _, kind, amount -> Pair(kind, amount) }
            .switchMapSingle { (kind, amount) ->
                expensesRepository
                    .add(OffsetDateTime.now(), amount.toLong(), kind)
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