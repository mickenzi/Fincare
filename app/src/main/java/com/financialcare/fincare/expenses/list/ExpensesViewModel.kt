package com.financialcare.fincare.expenses.list

import java.time.OffsetDateTime
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import com.financialcare.fincare.common.rx.either
import com.financialcare.fincare.common.rx.filterLeft
import com.financialcare.fincare.common.rx.filterRight
import com.financialcare.fincare.expenses.Expense
import com.financialcare.fincare.expenses.ExpensesError
import com.financialcare.fincare.expenses.ExpensesRepository
import com.financialcare.fincare.expenses.FilterParams
import com.financialcare.fincare.expenses.PaginationParams
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observable.just
import io.reactivex.rxjava3.subjects.PublishSubject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val expensesRepository: ExpensesRepository
) : ViewModel() {
    fun loadMore(lastId: String?, lastTime: OffsetDateTime?) {
        expensesEventSubject.onNext(ExpensesEvent.LoadMore(PaginationParams(lastId, lastTime)))
    }

    fun filter(filterParams: FilterParams) {
        expensesEventSubject.onNext(ExpensesEvent.Filter(filterParams))
    }

    fun reload() {
        expensesEventSubject.onNext(ExpensesEvent.Reload)
    }

    fun reset() {
        expensesEventSubject.onNext(ExpensesEvent.Reset)
    }

    fun delete(expense: Expense) {
        selectedExpenseSubject.onNext(expense)
    }

    fun confirmDelete() {
        confirmDeleteSubject.onNext(Unit)
    }

    fun rejectDelete() {
        rejectDeleteSubject.onNext(Unit)
    }

    val expenses: Observable<List<Expense>>
    val error: Observable<ExpensesError>

    val selectedFilterParams: Observable<FilterParams>

    val startDeleting: Observable<Unit>

    val isLoading: Observable<Boolean>

    val isRefreshing: Observable<Boolean>

    val areExpensesEmpty: Observable<Boolean>

    private val expensesEventSubject = PublishSubject.create<ExpensesEvent>()

    private val selectedExpenseSubject = PublishSubject.create<Expense>()

    private val confirmDeleteSubject = PublishSubject.create<Unit>()
    private val rejectDeleteSubject = PublishSubject.create<Unit>()

    init {
        startDeleting = selectedExpenseSubject.switchMap { just(Unit) }

        val deletedExpense = confirmDeleteSubject
            .withLatestFrom(selectedExpenseSubject) { _, item -> item }
            .switchMapSingle {
                expensesRepository
                    .delete(it)
                    .either<Unit, ExpensesError>()
            }
            .replay(1)
            .refCount()

        val expensesEvent = expensesEventSubject
            .mergeWith(deletedExpense.filterRight().map { ExpensesEvent.Reload })
            .startWithItem(ExpensesEvent.LoadMore(PaginationParams.empty))

        val queryParams = expensesEvent
            .scan(Pair(PaginationParams.empty, FilterParams.empty)) { acc, next ->
                when (next) {
                    is ExpensesEvent.Reset -> Pair(PaginationParams.empty, FilterParams.empty)
                    is ExpensesEvent.Reload -> Pair(PaginationParams.empty, acc.second)
                    is ExpensesEvent.LoadMore -> Pair(next.pageParams, acc.second)
                    is ExpensesEvent.Filter -> Pair(PaginationParams.empty, next.filterParams)
                }
            }
            .replay(1)
            .autoConnect()

        val loadedExpenses = queryParams
            .switchMapSingle { (pageParams, filterParams) ->
                expensesRepository
                    .all(pageParams, filterParams)
                    .either<List<Expense>, ExpensesError>()
            }
            .replay(1)
            .refCount()

        expenses = loadedExpenses
            .filterRight()
            .withLatestFrom(queryParams.map { (pageParams, _) -> pageParams }, ::Pair)
            .scan<List<Expense>>(emptyList()) { acc, (expenses, pageParams) ->
                if (pageParams.lastId.isNullOrEmpty()) {
                    expenses
                } else {
                    acc + expenses
                }
            }
            .skip(1)

        error = loadedExpenses
            .filterLeft()
            .mergeWith(deletedExpense.filterLeft())

        selectedFilterParams = queryParams.map { it.second }

        isLoading = loadedExpenses
            .filterRight()
            .map { false }
            .mergeWith(error.map { false })

        isRefreshing = expensesEventSubject
            .filter { it is ExpensesEvent.Reload }
            .map { true }
            .mergeWith(
                loadedExpenses
                    .map { false }
                    .skip(1)
            )

        areExpensesEmpty = expenses.map(List<Expense>::isEmpty)
    }

    private sealed interface ExpensesEvent {
        data class LoadMore(val pageParams: PaginationParams) : ExpensesEvent

        data class Filter(val filterParams: FilterParams) : ExpensesEvent

        data object Reload : ExpensesEvent

        data object Reset : ExpensesEvent
    }
}