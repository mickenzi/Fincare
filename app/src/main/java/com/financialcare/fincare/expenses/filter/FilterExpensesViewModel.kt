package com.financialcare.fincare.expenses.filter

import androidx.lifecycle.ViewModel
import com.financialcare.fincare.expenses.FilterParams
import com.financialcare.fincare.kinds.Kind
import com.financialcare.fincare.kinds.KindsRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

class FilterExpensesViewModel : ViewModel() {
    fun selectKinds(kinds: Set<String>) {
        selectedKindsSubject.onNext(kinds)
    }

    fun apply() {
        applySubject.onNext(Unit)
    }

    fun clear() {
        clearSubject.onNext(Unit)
    }

    fun setFilterParams(params: FilterParams) {
        selectedKindsSubject.onNext(params.kinds)
    }

    val filterParams: Observable<FilterParams>

    val areFilterParamsSelected: Observable<Boolean>

    val kinds: Array<Kind> = KindsRepository.kinds.toTypedArray()

    val selectedKinds: Observable<Set<String>>

    private val selectedKindsSubject = BehaviorSubject.create<Set<String>>()

    private val applySubject = PublishSubject.create<Unit>()
    private val clearSubject = PublishSubject.create<Unit>()

    init {
        selectedKinds = selectedKindsSubject
            .mergeWith(clearSubject.map { emptySet() })

        filterParams = applySubject
            .withLatestFrom(selectedKinds) { _, kinds -> FilterParams(kinds) }

        areFilterParamsSelected = selectedKinds.map(Set<String>::isNotEmpty)
    }
}